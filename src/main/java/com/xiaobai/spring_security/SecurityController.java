package com.xiaobai.spring_security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecurityController {

    @RequestMapping("/login")//这是Spring Security配置的登录页面地址，需要SpringMVC配合完成页面跳转
    public String login() {
        return "login";
    }//有了SpringMVC配置，这里到达login.jsp

    @RequestMapping("/success")
    public String success() {
        return "success";
    }

    @RequestMapping("/success1")
    public String success1() {
        return "success1";
    }
}
