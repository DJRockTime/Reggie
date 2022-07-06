package com.app.service.impl;

import com.app.common.BaseContext;
import com.app.common.CustomerException;
import com.app.entiry.*;
import com.app.mapper.OrdersMapper;
import com.app.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders 订单
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();

        // 查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomerException("购物车为空，不能下单");
        }

        // 查询用户数据
        User user = userService.getById(userId);

        // 向订单表插入数据，一条数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomerException("用户地址信息有误，不能下单");
        }

        // 通过 idWorker 生成一个订单号
        long orderId = IdWorker.getId();

        // 原子类，保证线程安全
        AtomicInteger amount = new AtomicInteger(0);

        // 计算购物车总金额
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get())); // 总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getPhone());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) +
                (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()) +
                (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        this.save(orders);

        // 向订单明细表插入一条数据， 多条数据
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车
        shoppingCartService.remove(queryWrapper);

    }
}
