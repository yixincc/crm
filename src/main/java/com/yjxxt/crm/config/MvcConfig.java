package com.yjxxt.crm.config;

import com.yjxxt.crm.interceptors.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //配置拦截器
        //添加拦截路径
        registry.addInterceptor(noLoginInterceptor())
                //拦截
                .addPathPatterns("/***")
                //放行
                .excludePathPatterns("/index","/user/login","/js/**","/css/**","/images/***","/lib/**");
    }
}
