package com.app.service.impl;

import com.app.common.CustomerException;
import com.app.dto.SetMealDto;
import com.app.entiry.SetMeal;
import com.app.entiry.SetMealDish;
import com.app.mapper.SetMealMapper;
import com.app.service.SetMealDishService;
import com.app.service.SetMealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {


    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setMealDto 实体类
     */
    @Override
    @Transactional
    public void saveWithDish(SetMealDto setMealDto) {
        // 保存套餐的基本信息
        this.save(setMealDto);

        List<SetMealDish> setMealDishes = setMealDto.getSetMealDishes();

        setMealDishes.stream().peek(item -> item.setSetMealId(setMealDto.getId())).collect(Collectors.toList());

        // 保存套餐和菜品的关联信息，操作setMealDish， 执行insert操作
        setMealDishService.saveBatch(setMealDishes);
    }

    /**
     * 删除套餐， 通过删除ids，删除套餐关联菜品
     *
     * @param ids 关联id
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //select count(*) from set_meal where id in ids and status = 1;

        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();

        // 构造查询条件
        queryWrapper.in(SetMeal::getId, ids);
        queryWrapper.eq(SetMeal::getStatus, 1);

        int count = (int) this.count(queryWrapper);
        if (count > 0) {
            // 如果不能删除，抛出一个业务异常信息
            throw new CustomerException("套餐正在售卖中，不能删除");
        }

        // 如果可以删除,先删除套餐表中的数据 -- setMeal
        this.removeByIds(ids);


        //select * from set_meal_dish where set_meal_id in (1, 2, 3);
        LambdaQueryWrapper<SetMealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetMealDish::getSetMealId, ids);
        // 删除关系表中的数据 -- setMealDish
        setMealDishService.remove(lambdaQueryWrapper);
    }
}
