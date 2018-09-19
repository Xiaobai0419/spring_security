package com.xiaobai.spring_security.config.service;

import com.xiaobai.spring_security.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Slf4j
public class SpringDataUserDetailsService implements UserDetailsService {

//    @Autowired
//    private DataSource dataSource;
//
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        System.out.println("-------------------------------->Arrived Here:JdbcAuth!!!!!!!!!!!!!!!!!!!!!!Username:" + s);
        //认证信息
        String sql = " select id,sso_id,password,state from app_user where sso_id = ? ";
        //注意queryForList,queryForObject一类方法只能查询表的单列，无法多列，再映射对象，多列报错！

        List<User> list = jdbcTemplate.query(sql,new Object[]{s},new RowMapper<User>() {
            User user = new User();
            @Nullable//根据源码传入参数，i代表每次迭代结果集的序号，从0开始
            public User mapRow(ResultSet resultSet, int i) throws SQLException {//源码：结果集while(resultSet.next()),每次返回一行，由mapRow方法实现处理一行的数据
                user.setId(resultSet.getInt("id"));
                user.setSso_id(resultSet.getString("sso_id"));
                user.setPassword(resultSet.getString("password"));
                user.setState(resultSet.getString("state"));
                return user;
            }//JDBC模块源码：处理每行，无匹配行则无User返回，list为空集合
        });
        final User user = list != null && list.size() > 0 ? list.get(0) : null;
        log.info("User From DataBase:" + (user != null ? user.toString() : null));
        //数据库有相同用户名的用户（密码不在这里验证，是另一个模块的任务）
        if(user != null) {
            //权限信息
            sql = " select c.type from user_profile c where exists (select b.user_id from  app_user_user_profile b where b.user_profile_id = c.id and b.user_id = ?) ";
            sql = " select c.type from user_profile c where c.id = (select b.user_profile_id from  app_user_user_profile b where b.user_profile_id = c.id and b.user_id = ?) ";
            sql = " select c.type from user_profile c where c.id in (select b.user_profile_id from  app_user_user_profile b where b.user_id = ?) ";//这里要用in,如果用=,报错：子查询返回多行
            sql = " select c.type from user_profile c where c.id = any (select b.user_profile_id from  app_user_user_profile b where b.user_id = ?) ";
            sql = " select c.type from user_profile c where c.id = some (select b.user_profile_id from  app_user_user_profile b where b.user_id = ?) ";//以上语句在这里等价，这里不能用all,语法不报错，但会返回空行
            jdbcTemplate.query(sql, new Object[]{user.getId()}, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    user.getAuthorities().add(new SimpleGrantedAuthority("ROLE_" + resultSet.getString(1)));
                }
            });
            log.info("User And User Autherization From DataBase:" + user.toString());
            org.springframework.security.core.userdetails.User sec_user = new org.springframework.security.core.userdetails.User(user.getSso_id(), user.getPassword(),
                    user.getState().equals("Active"), true, true, true, user.getAuthorities());//最后一个参数是该认证用户的权限集合
//第三个参数判断true/false,这里是与业务相关的一个字段表示该账户是否为活跃，活跃的才验证成功
            log.info("User Of Spring Security:" + sec_user);
            return sec_user;
        }else {
            //没有该用户名用户，根据上层异常处理逻辑，这里直接抛出UsernameNotFoundException异常
            throw new UsernameNotFoundException("未找到该用户信息！");
        }
    }

    public static void main(String[] args) {
        org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()//创建了一个加密方式为bcrypt的加密代理,enode方法用于使用该加密方式加密;将该代理的encode方法（作为一个Function）以Function实例的方式传递给Builder的该Function类型字段
                .username("user")
                .password("password")
                .roles("user")
                .build();//使用Function类型字段的apply传入password,相当于调用bcrypt加密代理的encode方法加密password,有关Function和lambda表达式参照博客园引用博文：()->{}代表接口匿名实例，()内是方法参数，单个参数省略括号，{}内为方法实现，一句的return省略return和{},所以Function这种使用Lambda表达的实现可以为：e->e*2,表示其唯一抽象方法（这样的接口称为函数式接口）apply方法实现为apply(e){return e*2;}
    }
}
//exists,in,any,=,some,all用法参照：https://blog.csdn.net/tjuyanming/article/details/77015427 https://www.cnblogs.com/feiquan/p/8654171.html