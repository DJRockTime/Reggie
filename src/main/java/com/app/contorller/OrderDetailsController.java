package com.app.contorller;


import com.app.common.R;
import com.app.entiry.Orders;
import com.app.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/orderDetails")
public class OrderDetailsController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders 订单实体
     * @return R<String>
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody  Orders orders) {
        log.info("订单数据: {} ", orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }
}
