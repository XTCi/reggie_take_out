package com.example.reggie_take_out.contorller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ServletComponentScan
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController{
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        将页面提交的代码进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        根据用户名进行查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        如果没有查询到则返回登录失败
        if(emp == null){
            return R.error("登录失败，没有该账号");
        }
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败,密码错误");
        }
        if(emp.getStatus() == 0){
            return R.error("账户已禁用");
        }
        request.getSession().setAttribute("employee",emp.getId());



        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        long empid = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);
        employeeService.save(employee);
        return R.success("新增员工成功");

    }

    /**
     *员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page (int page,int pageSize,String name){
        log.info("page{},pageSize{},name{}",page,pageSize,name);
//        构造分页构造器
        Page pageinfo = new Page(page,pageSize);
//        构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
//        添加构造条件
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
//        添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
//        执行查询
        employeeService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);

    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");


    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id来查询你员工信息");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

}
