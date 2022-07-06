package com.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.app.dto.DishDto;
import com.app.entiry.Dish;
import com.app.entiry.DishFlavor;
import com.app.mapper.DishMapper;
import com.app.service.DishFlavorService;
import com.app.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishDtoId = dishDto.getId();

        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //flavors = flavors.stream().map((item) -> {
        //    item.setDishId(dishDtoId);
        //    return item;
        //}).collect(Collectors.toList());

        flavors = flavors.stream().peek((item) -> item.setDishId(dishDtoId)).collect(Collectors.toList());

        // 保存菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id 传入id
     * @return dishDto
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息 （菜品表)
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询当前菜品对应的口味信息，从dish_flavor表查询 (口味表)
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        System.out.println(flavors);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应的口味数据  dish_flavor 表的delete
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(lambdaQueryWrapper);

        // 添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 当前只保存了name value 值， 但是没有dishID，
        //flavors.stream().map(item -> {
        //    item.setDishId(dishDto.getId());
        //    return item;
        //}).collect(Collectors.toList());
        flavors.stream().peek(item -> item.setDishId(dishDto.getId())).collect(Collectors.toList());

        //进行批量保存
        dishFlavorService.saveBatch(flavors);
    }
}
