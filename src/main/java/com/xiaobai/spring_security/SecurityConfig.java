package com.xiaobai.spring_security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
//                .anyRequest().authenticated()//这里会导致loginPage也无法访问，那么只能在最后加上permitAll，但这样又会导致下面的hasRole权限配置都不生效，验证后无需拥有对应Role就可访问这些页面，所以这里注释掉！
                .antMatchers("/css/**").permitAll()
                .antMatchers("/success/**").hasRole("USER")
                .antMatchers("/success1/**").hasRole("USER1")//经测试，这里如果不规定权限，这个相应页面可以在上面.anyRequest().authenticated()注释掉的情况下，无登录授权下直接访问！根据官网，如果添加.anyRequest().authenticated()，没有规定权限的页面均可在登录验证通过后即可访问：Any URL that has not already been matched on only requires that the user be authenticated
                .and()
                .formLogin()
                .loginPage("/login");//经测试，首页无论以项目根路径或index.jsp访问，都是无需登录验证即可访问，且登录后默认跳转到根路径，即首页！
    }

    @Autowired//注入@EnableWebSecurity创建的AuthenticationManagerBuilder，并设置它，这里并不涉及Bean的创建，或什么接口继承，这个方法也可以是任何方法，只要注入AuthenticationManagerBuilder并设置了权限信息即可，Spring要用的就是这个容器中创建的AuthenticationManagerBuilder
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}
