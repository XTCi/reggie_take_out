package com.example.reggie_take_out.filter;


import com.alibaba.fastjson.JSON;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
//    路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        获取本次请求的url
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
//        定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };
//        判断本次请求是否要被处理
        boolean check = check(urls,requestURI);

//        如果不需要就放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        判断是否登录，登录就放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));
            Long empID = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrendId(empID);



            filterChain.doFilter(request,response);
            return;
        }
//        移动端
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));
            Long userID = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrendId(userID);



            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
//        未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

        public boolean check(String[] urls,String requestURI){
            for (String url:urls){
                boolean match = PATH_MATCHER.match(url,requestURI);
                if(match){
                    return true;
                }

            }
            return false;
        }
}
