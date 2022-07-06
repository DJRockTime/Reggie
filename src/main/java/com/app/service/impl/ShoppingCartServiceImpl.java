package com.app.service.impl;

import com.app.entiry.ShoppingCart;
import com.app.mapper.ShoppingCartMapper;
import com.app.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
