package com.example.reggie_take_out.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component//被标记时，spring会i自动化扫描，并将其作为一个Bean纳入到上下文中
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 插入公共字段
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充[insert]");
        log.info(metaObject.toString());
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrendId());
        metaObject.setValue("updateUser",BaseContext.getCurrendId());
    }

    /**
     * 更新公共字段
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrendId());


    }
}
