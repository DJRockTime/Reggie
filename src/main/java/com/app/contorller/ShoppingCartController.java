package com.app.contorller;

import com.app.common.BaseContext;
import com.app.common.R;
import com.app.entiry.ShoppingCart;
import com.app.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    private R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据: {} ", shoppingCart);

        // 设置用户id，指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);


        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if (dishId != null) {
            // 添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            // 添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // 查询当前菜品或者套餐是否在购物车中
        /*
         * select * from shopping_cart where user_id = ? and dish_id ?  / set_meal_id = ?
         * */
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            // 如果已经存在，就在原来的数量的基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 如果不存在，泽田见到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(cartServiceOne);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 产看购物车
     * @return List<ShoppingCart>
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info(" 查看购物车... ");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return R<String>
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        /*
        * delete from shopping_cart where user_id = ?
        * */
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
