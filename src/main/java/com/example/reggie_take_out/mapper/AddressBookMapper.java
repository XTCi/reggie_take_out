package com.example.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie_take_out.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Bean;
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
