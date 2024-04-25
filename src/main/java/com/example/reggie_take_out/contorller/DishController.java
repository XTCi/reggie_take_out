package com.example.reggie_take_out.contorller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishFlavorService;
import com.example.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品，用DTO
     * @param dishDto
     * @return
     */

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithflavor(dishDto);

        return R.success("新增菜品成功");

    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
//        构造分页构造器
        Page<Dish> pageinfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage = new Page<>();
//        条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
//        添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageinfo,queryWrapper);
//        对象拷贝
        BeanUtils.copyProperties(pageinfo,dishDtoPage,"records");
        List<Dish> records = pageinfo.getRecords();
        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);

    }

    @GetMapping("{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithflavor(dishDto);

        return R.success("修改菜品成功");

    }

    /**
     * 停售代码
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> ban_status(@PathVariable("status") Integer status,Long[] ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = dishService.list(queryWrapper);
        for (Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }

        }
        return R.success("售卖状态修改成功");

    }
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithdishflavor(ids);

        return R.success("删除成功");

    }
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
////        构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
////        添加起售条件
//        queryWrapper.eq(Dish::getStatus,1);
////        添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }



    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        添加起售条件
        queryWrapper.eq(Dish::getStatus,1);
//        添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);



        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtos = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }
}
