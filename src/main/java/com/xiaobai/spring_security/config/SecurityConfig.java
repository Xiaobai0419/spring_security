package com.xiaobai.spring_security.config;

import com.xiaobai.spring_security.config.service.SpringDataUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
//@PropertySource(value = { "classpath:application.properties" })
public class SecurityConfig {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
////                .anyRequest().authenticated()//这里会导致loginPage也无法访问，那么只能在最后加上permitAll，但这样又会导致下面的hasRole权限配置都不生效，验证后无需拥有对应Role就可访问这些页面，所以这里注释掉！
//                .antMatchers("/css/**").permitAll()
//                .antMatchers("/success/**").hasRole("USER")
//                .antMatchers("/success1/**").hasRole("USER1")//经测试，这里如果不规定权限，这个相应页面可以在上面.anyRequest().authenticated()注释掉的情况下，无登录授权下直接访问！根据官网，如果添加.anyRequest().authenticated()，没有规定权限的页面均可在登录验证通过后即可访问：Any URL that has not already been matched on only requires that the user be authenticated
//                .and()
//                .formLogin()
//                .loginPage("/login");//经测试，首页无论以项目根路径或index.jsp访问，都是无需登录验证即可访问，且登录后默认跳转到根路径，即首页！
//    }

//    @Autowired
//    private Environment environment;//Spring容器环境
//
    @Bean
    public DataSource dataSource() {
//        System.out.println(environment);
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
//        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
//        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
//        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
//        return dataSource;
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        System.out.println("------------------------------------->DriverManagerDataSource Initialized:" + dataSource);
        return dataSource;
    }

    @Bean
    @Autowired
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        System.out.println("------------------------------------->JdbcTemplate Initialized:" + jdbcTemplate);
        return jdbcTemplate;
    }

    @Bean
    public UserDetailsService springDataUserDetailsService() {
        SpringDataUserDetailsService springDataUserDetailsService = new SpringDataUserDetailsService();
        System.out.println("------------------------------------->UserDetailsService Initialized:" + springDataUserDetailsService);
        return springDataUserDetailsService;
    }

    @Autowired
    UserDetailsService userDetailsService;

    @Configuration
    @Order(1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {//这种配置必须继承WebSecurityConfigurerAdapter
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
    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Autowired//注入@EnableWebSecurity创建的AuthenticationManagerBuilder，并设置它，这里并不涉及Bean的创建，或什么接口继承，这个方法也可以是任何方法，只要注入AuthenticationManagerBuilder并设置了权限信息即可，Spring要用的就是这个容器中创建的AuthenticationManagerBuilder
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //默认不加密匹配，或这里设置过时的NoOpPasswordEncoder.getInstance()也是不加密直接匹配，设置new BCryptPasswordEncoder()是直接按bcrypt算法匹配，不带{算法前缀}
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());//这里配置UserDetailsService和PasswordEncoder，权限验证才会生效，这里配置为bcrypt方式加密，只涉及matches方法相应算法匹配用户输入原生密码与用户自定义查询数据库注入到UserDetails的该算法加密密码
//        //1.
//        auth
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER");

        //2.
        // ensure the passwords are encoded properly
//        User.UserBuilder users = User.withDefaultPasswordEncoder();
//        auth
//                .jdbcAuthentication()
//                .dataSource(dataSource)
//                .withDefaultSchema()
//                .withUser(users.username("user").password("password").roles("USER"))
//                .withUser(users.username("admin").password("password").roles("USER","ADMIN"));//经测试，上面代码需要Spring Security相关依赖（core,web,config）均为5以上版本
/**
 * 官网：这种方式适合内存方式，自己生成用户加密密码，采用DelegatingPasswordEncoder进行加密和匹配，加密后的密码结构为：{算法前缀}+算法加密密码，匹配时，设置DelegatingPasswordEncoder到SecurityConfigurer，即上面configureGlobal中的PasswordEncoder配置，匹配时也会判断PasswordEncoder配置以此除去加密密码的{算法前缀}，再按DelegatingPasswordEncoder中设置的算法进行匹配
 User user = User.withDefaultPasswordEncoder()
 .username("user")
 .password("password")
 .roles("user")
 .build();
 System.out.println(user.getPassword());
 // {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
  */

    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String password = "123456";
        String bcryptPassword = bcrypt.encode(password);
        System.out.println("bcryptPassword:" + bcryptPassword);//每次生成的密码并不相同，但可以匹配，数据库存储一个加密后的密码即可
    }
}
