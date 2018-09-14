package com.xiaobai.spring_security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer
        extends AbstractSecurityWebApplicationInitializer {//Servlet3.0自动侦测该类，并加载Spring容器

//    public SecurityWebApplicationInitializer() {
//        super(SecurityConfig.class);
//    }//Spring容器加载该配置类，启动Spring Security权限拦截
}
/**官网：
 The SecurityWebApplicationInitializer will do the following things:

 Automatically register the springSecurityFilterChain Filter for every URL in your application

 Add a ContextLoaderListener that loads the SecurityConfig.
 */