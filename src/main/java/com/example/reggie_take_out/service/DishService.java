package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
//    新增菜品，同时插入菜品对应的口味数据：需要操作两种表：dish，dishflavor
    public void saveWithflavor(DishDto dishDto);
//    根据id来查询菜品信息和口味信息
    public DishDto getWithFlavor(Long id);
//    更新菜品信息和口味信息
    public  void updateWithflavor(DishDto dishDto);
    public void deleteWithdishflavor(List<Long> ids);
}
