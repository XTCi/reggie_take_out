package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.mapper.SetmealMapper;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除菜品与套餐关系表
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);
    /**
     * 根据id进行查询，用来回显数据
     * @param id
     */
    public SetmealDto  getbyidWithDish(Long id);

    /**
     * 根据回显的结果进行修改
     * @param setmealDto
     */
    public void  updateWithDish(SetmealDto setmealDto);
    public void updateSetmealStatusById(Integer status,List<Long> ids);
}
