package com.example.reggie_take_out.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie_take_out.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
