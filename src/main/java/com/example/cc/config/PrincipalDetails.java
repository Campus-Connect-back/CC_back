package com.example.cc.config;

import com.example.cc.entity.usersEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PrincipalDetails implements UserDetails {

    private usersEntity user;

    // 권한 관련 작업을 하기 위한 role return
    public PrincipalDetails(usersEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_USER");
    }
    // get Password 메서드
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // get Username 메서드, Long타입 학번을 string을 변환
    @Override
    public String getUsername() {
        return String.valueOf(user.getStudentId().getStudentId());
    }

    // 계정이 만료 되었는지 (true: 만료X)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겼는지 (true: 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호가 만료되었는지 (true: 만료X)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화(사용가능)인지 (true: 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }
}