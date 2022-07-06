package com.app.contorller;


import com.app.common.R;
import com.app.dto.SetMealDto;
import com.app.entiry.Category;
import com.app.entiry.SetMeal;
import com.app.service.CategoryService;
import com.app.service.SetMealDishService;
import com.app.service.SetMealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setMeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setMealDto 套餐DTO
     * @return String
     */
    @PostMapping
    public R<String> save(@RequestBody SetMealDto setMealDto) {

        log.info("套餐信息：{}", setMealDto);
        setMealService.saveWithDish(setMealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int pageNumber, int pageSize, String name) {
        //分页构造器
        Page<SetMeal> pageInfo = new Page<>(pageNumber, pageSize);
        Page<SetMealDto> dtoPage = new Page<>(pageNumber, pageSize);

        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，更具name进行like 模糊查询
        queryWrapper.like(name != null, SetMeal::getName, name);
        // 根据更新时间降序排序
        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        setMealService.page(pageInfo, queryWrapper);

        // 对象的拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<SetMeal> records = pageInfo.getRecords();

        List<SetMealDto> list = records.stream().map(item -> {
            SetMealDto setMealDto = new SetMealDto();

            // 对象拷贝
            BeanUtils.copyProperties(item, setMealDto);

            // 分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                // 分类名称
                String categoryName = category.getName();
                setMealDto.setCategoryName(categoryName);
            }
            return setMealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids 套餐id
     * @return String
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setMealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param setMeal 套餐实体
     * @return List<SetMeal>
     */
    public R<List<SetMeal>> list(SetMeal setMeal) {
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setMeal.getCategoryId() != null, SetMeal::getCategoryId, setMeal.getCategoryId());
        queryWrapper.eq(setMeal.getStatus() != null, SetMeal::getStatus, setMeal.getStatus());

        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        List<SetMeal> list = setMealService.list(queryWrapper);
        return R.success(list);
    }

}
