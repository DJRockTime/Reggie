package com.app.contorller;


import com.app.common.R;
import com.app.dto.DishDto;
import com.app.entiry.Category;
import com.app.entiry.Dish;
import com.app.entiry.DishFlavor;
import com.app.entiry.SetMealDish;
import com.app.service.CategoryService;
import com.app.service.DishFlavorService;
import com.app.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDto 菜品数据
     * @return String "新增菜品成功"
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info(String.valueOf(dishDto));
        dishService.saveWithFlavor(dishDto);
        // 清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        // 精确清理 清理某个分类下面的菜品缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);


        return R.success("新增菜品成功");
    }


    @GetMapping("/page")
    public R<Page<DishDto>> page(int pageNumber, int pageSize, String name) {

        Page<Dish> pageInfo = new Page<>(pageNumber, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(pageNumber, pageSize);

        // 构造分页构造器对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);

        // 添加排序
        queryWrapper.orderByDesc(Dish::getCreateTime);

        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map(item -> {
            System.out.println(item);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto 菜品数据
     * @return String "新增菜品成功"
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        log.info(String.valueOf(dishDto));
        dishService.updateWithFlavor(dishDto);

        // 清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        // 精确清理 清理某个分类下面的菜品缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);


        return R.success("新增菜品成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish 菜品
     * @return 返回List
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 菜品id = 传进来的id
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(dish.getSort() != null, Dish::getSort, dish.getSort());

        // 添加条件， 查询状态为1的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 根据条件查询菜品数据
     * @param dish 菜品扩展实体类
     * @return 返回List
     */
    @GetMapping("/phoneList")
    public R<List<DishDto>> phoneList(Dish dish) {

        List<DishDto> dishDtoList = null;

        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus(); // dish_15645616158915_1

        // 先从redis中获取缓存数据
         dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

         if(dishDtoList != null) {
             // 如果存在，直接返回，无需查询数据库
             return R.success(dishDtoList);
         }

        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 菜品id = 传进来的id
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(dish.getSort() != null, Dish::getSort, dish.getSort());

        // 添加条件， 查询状态为1的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map(item -> {
            System.out.println(item);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            // 当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            // SQL: select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        // 如果不存在，需要查询数据库，将查询到的数据缓存到数据库
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


}
