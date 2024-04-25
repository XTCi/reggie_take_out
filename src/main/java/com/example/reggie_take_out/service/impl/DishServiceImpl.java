package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.mapper.DishMapper;
import com.example.reggie_take_out.service.DishFlavorService;
import com.example.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
    /**
     * 新增菜品同时保存对于的口味数据
     * @param dishDto
     */

    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    public void saveWithflavor(DishDto dishDto) {
//        保存菜品到dish
        this.save(dishDto);
        Long dishId = dishDto.getId();

//        菜品口味便利出来进行id加工再付给他自己
        List<DishFlavor> flavors= dishDto.getFlavors();
        flavors.stream().map((itrm) -> {
            itrm.setDishId(dishId);
            return itrm;
        }).collect(Collectors.toList());
//        保存菜品口味到dishflavor
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    /**
     * 根据id来查询菜品信息和口味信息
     * @param id
     * @return
     */
    public DishDto getWithFlavor(Long id) {
//        查询菜品基本信息，从dish
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

//        查询菜品口味信息,从dishflacor
//        构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
//        构造条件
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavor = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavor);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithflavor(DishDto dishDto) {
//        更新dish表
        this.updateById(dishDto);
//        清理当前菜品口味表-dishflavor的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        添加当前提交过来的口味数据,insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((itrm) -> {
            itrm.setDishId(dishDto.getId());
            return itrm;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);


    }
    @Override
    @Transactional
    public void deleteWithdishflavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int cout =  this.count(queryWrapper);
        if(cout>0){
            throw new CustomException("菜品正在售卖，菜品不可删除");
        }
        this.removeByIds(ids);

//        删除对应口味表
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
    }
}
