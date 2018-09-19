package com.xiaobai.spring_security.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private long id;
    private String sso_id;
    private String password;
    private String state;
    private Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

}
