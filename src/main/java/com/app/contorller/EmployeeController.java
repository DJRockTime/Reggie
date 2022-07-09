package com.app.contorller;


import com.app.common.R;
import com.app.entiry.Employee;
import com.app.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        /*
         * 1. 将页面提交的密码password 进行md5 加密
         * 2. 根据页面提交的用户username 查询数据库
         * 3. 如果没有查询到则返回登录失败的结果
         * 4. 密码比对，如果不一致则返回登录失败结果
         * 5. 查看员工状态，如果已为禁用，则返回员工已禁用结果
         * 6. 登录成功， 将员工id存入session 并返回登录成功结果
         * */

        // 1
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3.
        if (emp == null) {
            return R.error("登录失败");
        }

        // 4.
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        // 5.
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

        // 6
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        // 清除 session 中保存的当前登录的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工
     *
     * @param employee 员工信息
     * @return R<String>
     * @CachePut 将方法返回值放入缓存
     * value : 缓存的名称，每个缓存名称下面可以有多个key
     * key: 缓存的key
     */
    @CachePut(value = "userCache", key = "#employee.id")
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("add customer");

        // 设置初始密码，需要进行MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //
        //// 获得当前用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功！");
    }

    /**
     * 员工信息的分页查询
     *
     * @param pageNumber
     * @param pageSize
     * @param name
     * @return
     * Cacheable 在方法执行前spring查看缓存中是否有数据，如果有数据，则直接返回数据，如果没有数据，到库查询
     * condition: 条件， 满足条件时才缓存数据
     *
     */
    //@Cacheable(value = "userCache", key = "#id", condition = "#result != null")
    //@Cacheable(value = "userCache", key = "#user.id + '_' + #user.name")
    @GetMapping
    public R<Page> page(int pageNumber, int pageSize, String name) {

        log.info("{}, {}, {}", pageNumber, pageSize, name);
        /*
         * 1. 构造分页器
         * 2. 构造条件构造器
         * 4. 执行查询
         * */

        // 1.
        Page pageInfo = new Page(pageNumber, pageSize);

        // 2.
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 3. 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 4 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信息
     *
     * @param employee 员工
     * @return R.success
     */
    @CacheEvict(value = "userCache", key = "#employee.id") // 参数
    //@CacheEvict(value = "userCache", key = "#p1.id") // 参数
    //@CacheEvict(value = "userCache", key = "#root.args[0].id") // 参数
    //@CacheEvict(value = "userCache", key = "#result.id") // 参数
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        System.out.println(employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        //LambdaQueryWrapper
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if (emp != null) {
            return R.success(emp);
        }
        return R.error("没有查询到对饮员工信息");
    }

    /**
     * 删除员工
     * @param id 员工id
     * @return String
     * CacheEvict 清理指定缓存
     */
    @CacheEvict(value = "userCache", key = "#id") // 参数
    //@CacheEvict(value = "userCache", key = "#p0") // 第一个参数
    //@CacheEvict(value = "userCache", key = "#root.args[0]") // 第一个参数
    @DeleteMapping("/{id}")
    public R<String> deleteById(@PathVariable Long id) {
        employeeService.removeById(id);
        return R.success("删除成功");
    }
}
