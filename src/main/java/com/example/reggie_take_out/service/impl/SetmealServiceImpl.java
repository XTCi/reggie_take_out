package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.mapper.SetmealMapper;
import com.example.reggie_take_out.service.SetmealDishservice;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishservice setmealDishservice;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(@RequestBody SetmealDto setmealDto) {
//        保存套餐的信息，操作Setmeal，执行insert
        this.save(setmealDto);


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
//        保存菜品和套餐的关系信息，操作Setmeal_Dish,执行insert
        setmealDishservice.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，同时删除菜品与套餐关系表
     * @param ids
     */
    @Transactional
    public void deleteWithDish(List<Long> ids) {
//        查看当前套餐的状态，判断是否可以删除，
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = count(queryWrapper);
        if(count>0){
//            如果不能，抛出一个异常
            throw new CustomException("套餐正在售卖，不能删除");

        }
//        如果可以则进行删除套餐表
        this.removeByIds(ids);
//        然后删除套餐与菜品关系表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishservice.remove(lambdaQueryWrapper);




    }

    /**
     * 根据id进行查询，用来回显数据
     * @param id
     */
    @Override
    @Transactional
    public SetmealDto  getbyidWithDish(Long id) {
//        根据id查询setmeal表
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
//        对象拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);
//        查询菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealdishlist = setmealDishservice.list(queryWrapper);
//        设置菜品属性，把找到的菜品返回给setmealdto
        setmealDto.setSetmealDishes(setmealdishlist);
        return setmealDto;
    }
    /**
     * 根据回显的结果进行修改
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
//        保存setmeal表的基本元素
        this.updateById(setmealDto);
//        删除原来套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishservice.remove(queryWrapper);
//        跟新套餐关联表，先获得setmealsto中的菜品集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
//        重新保存套餐对于的菜品
        setmealDishservice.saveBatch(setmealDishes);



    }

    @Override
    @Transactional
    public void updateSetmealStatusById(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);

        for (Setmeal setmeal:list){
            if(setmeal != null){
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }
        }

    }

}
