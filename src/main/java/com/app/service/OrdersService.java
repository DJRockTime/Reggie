package com.app.service;

import com.app.entiry.Orders;
import com.app.mapper.OrdersMapper;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
