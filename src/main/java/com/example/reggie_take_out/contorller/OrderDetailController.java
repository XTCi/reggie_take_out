package com.example.reggie_take_out.contorller;

import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Orders;
import com.example.reggie_take_out.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderDetail")
@Slf4j
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;


}
