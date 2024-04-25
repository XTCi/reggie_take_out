package com.example.reggie_take_out.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.ShoppingCart;
import com.example.reggie_take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);
        //获取用户id，使用同一个进程的方法
        Long currendId = BaseContext.getCurrendId();
        shoppingCart.setUserId(currendId);
        //获取菜品id，判断是套餐还是菜品
        Long dishId = shoppingCart.getDishId();
        //添加查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currendId);

        if (dishId != null){
            //判断菜品在表里面是否存在
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {//套餐条件
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前菜品是否在购物车中
        ShoppingCart CartServiceOne = shoppingCartService.getOne(queryWrapper);
        //如果已经存在的话number加1
        if (CartServiceOne != null){
            Integer number = CartServiceOne.getNumber();
            CartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(CartServiceOne);
        }else {//如果不存在那就添加进去
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            CartServiceOne = shoppingCart;

        }
        return R.success(CartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrendId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        log.info("情况购物车");
        Long currendId = BaseContext.getCurrendId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currendId);
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车已经清空");

    }




}
