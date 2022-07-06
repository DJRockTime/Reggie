package com.app.service;

import com.app.dto.SetMealDto;
import com.app.entiry.SetMeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setMealDto 实体类
     */
    void saveWithDish(SetMealDto setMealDto);

    /**
     * 通过删除ids，删除套餐关联菜品
     * @param ids 关联id
     */
    void removeWithDish(List<Long> ids);
}
