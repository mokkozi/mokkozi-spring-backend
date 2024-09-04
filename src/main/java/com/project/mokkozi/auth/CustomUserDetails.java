package com.project.mokkozi.auth;

import com.project.mokkozi.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;    // PK seq
    private String loginId; // 로그인 아이디
    private String password;    // 로그인 비밀번호
    private String name;    // 사용자 이름
    // private boolean locked;  // 계정 잠김 여부
    private Collection<GrantedAuthority> authorities;   // 권한 목록

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails fromEntity(Member member) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.id = member.getId();
        customUserDetails.loginId = member.getLoginId();
        customUserDetails.password = member.getPassword();

        return customUserDetails;
    }
}
