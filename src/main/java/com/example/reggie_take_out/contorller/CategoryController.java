package com.example.reggie_take_out.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增菜品成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
//        分页构造器
        Page<Category> pageinfo = new Page<>(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        添加排序条件，以soet进行排序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }

    /**
     * 删除菜品分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("分类删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R.success("分类更新成功");
    }
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
//        构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        设置条件，type
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
//        添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);

    }

}

