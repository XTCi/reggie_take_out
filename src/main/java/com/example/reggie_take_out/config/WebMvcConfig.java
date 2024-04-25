package com.example.reggie_take_out.config;

import com.example.reggie_take_out.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("front/**").addResourceLocations("classpath:/front/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息器");
//        创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        设置消息转换器，底层使用jackson把Java对象转化为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        将上面的转化器追加到mvc框架集合中，需要设置id，调用靠前的
        converters.add(0,messageConverter);


    }
}
