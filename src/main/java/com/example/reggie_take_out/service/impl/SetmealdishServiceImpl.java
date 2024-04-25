package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.mapper.SetmealDishmapper;
import com.example.reggie_take_out.service.SetmealDishservice;
import org.springframework.stereotype.Service;

@Service
public class SetmealdishServiceImpl extends ServiceImpl<SetmealDishmapper, SetmealDish> implements SetmealDishservice {
}
