package com.example.reggie_take_out.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.User;
import com.example.reggie_take_out.service.UserService;
import com.example.reggie_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMeg(@RequestBody User user, HttpSession session){
//        获取手机号
        String phone = user.getPhone();


        if (StringUtils.isNotEmpty(phone)){
            //        生成四位数沿验证吗
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //        调用阿里云提供的短信服务api发送短信
//            SMSUtils.sendMessage("","");

            //        用session存储验证码
            session.setAttribute(phone,code);
            return R.success("手机验证码短信发送成功");

        }

        return R.error("验证码发送失败");
    }

    /**
     * 手机验证码登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
//        获取手机号
        String phone = map.get("phone").toString();
//        获取验证码
        String code = map.get("code").toString();
//        从session中获取保存的验证码
        Object codeinsession = session.getAttribute(phone);
//        进行验证码的比对（短信接收提交的和后端随机生成的）
        if (codeinsession != null && codeinsession.equals(code)){
            //        如果能比对成功，说明登录成功
            //        判断当前手机号是否是新用户，是的话直接注册
            LambdaQueryWrapper<User> queryWrapper  = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user  == null){
                user = new User();
                user.setPhone(phone);;
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
