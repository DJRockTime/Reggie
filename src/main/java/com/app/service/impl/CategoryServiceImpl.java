package com.app.service.impl;

import com.app.common.CustomerException;
import com.app.entiry.Category;
import com.app.entiry.Dish;
import com.app.entiry.Employee;
import com.app.entiry.SetMeal;
import com.app.mapper.CategoryMapper;
import com.app.mapper.EmployeeMapper;
import com.app.service.CategoryService;
import com.app.service.DishService;
import com.app.service.EmployeeService;
import com.app.service.SetMealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{


    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    /**
     * 根据id删除分类， 删除之前进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = (int) dishService.count(dishLambdaQueryWrapper);
        // 查询当前分类是否关联菜品，如果关联，抛出一个业务异常
        if(dishCount > 0) {
            // 已经关联，抛出业务异常
            throw new CustomerException("当前分类项关联了菜品，不能删除");
        }


        // 查询当前分类是否关联套餐，如果关联，抛出一个业务异常
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId, id);
        int setMealCount = (int) setMealService.count(setMealLambdaQueryWrapper);
        if(setMealCount > 0) {
            // 已经关联套餐，抛出业务异常
            throw new CustomerException("当前分类项关联了套餐，不能删除");
        }

        // 正常删除分类
        super.removeById(id);
    }
}
