package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishService;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        查询条件，根据id进行分类
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
//        查询当前是否关联菜品
        if(count1 > 0){
            throw new CustomException("当前分类下存在菜品，不能删除");

        }
//        查询当前是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            throw new CustomException("当前分类下存在套餐，不能删除");

        }



    }
}
