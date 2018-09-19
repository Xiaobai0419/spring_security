package com.xiaobai.spring_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc//这个注解用于启动SpringMVC一系列服务：转发Servlet,地址映射，处理器适配，视图解析，如果不配置，和Spring Security默认无关的一切地址映射、访问、转发和渲染均无效！包括自定义登录页面！SpringBoot不需要是因为只要它引入了Web依赖，会在启动时根据依赖自动配置SpringMVC模块！
@ComponentScan(basePackages = "com.xiaobai.spring_security")
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean(name="securityMvc")
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        System.out.println("--------------------------------------------->InternalResourceViewResolver Initialized!!!!!!!!!!!!!!!!!!!!!!");
        return viewResolver;
    }

    /*
     * Configure ResourceHandlers to serve static resources like CSS/ Javascript etc...
     *
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }
}
