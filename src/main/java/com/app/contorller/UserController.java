package com.app.contorller;


import com.app.common.R;
import com.app.entiry.User;
import com.app.service.UserService;
import com.app.utils.SMSUtils;
import com.app.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码
     * @param user 用户封装
     * @param session session信息
     * @return String
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(User user, HttpSession session) {

        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {

            // 生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code: {}: ", code);

            // 调用阿里云提供的换新服务API完成短信发送 // 一条短信0.45分
            //SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            // 需要将生成的验证码保存到Session
            session.setAttribute(phone, code);

            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map map对象
     * @param session session
     * @return String
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString()); // phone: 13566669999    code: 1234

        // 获取手机号
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = map.get("code").toString();

        // 从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        // 进行验证码比对，（页面提交的验证码和Session中保存的验证码比对)
        if(codeInSession != null && codeInSession.equals(code)) {
            // 如果能够对比成功, 说名登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper); // 空指针异常：User user = new User() 而非自己创建User，要从表中查询User信息，user = new User();

            if(user == null) {
                // 判断当亲啊手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }

}
