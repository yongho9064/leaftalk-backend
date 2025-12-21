package com.example.leaftalk.global.security.details;

import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.entity.Role;
import com.example.leaftalk.global.security.dto.AuthMember;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class MemberDetails implements UserDetails, OAuth2User {

    private final String email;
    private final String password;
    private final Role role;
    private final boolean isLock;

    private final transient Map<String, Object> attributes; // 소셜 로그인 사용자 정보

    // 일반 로그인
    public MemberDetails(Member member) {
        this(member, Collections.emptyMap());
    }

    // 소셜 로그인
    public MemberDetails(Member member, Map<String, Object> attributes) {
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.role = member.getRole();
        this.isLock = member.isLock();
        this.attributes = attributes;
    }

    // @LoginMember 어노테이션 사용 시 반환할 객체
    public AuthMember toAuthMember() {
        return new AuthMember(role, email);
    }

    // 공통 메서드 (일반 로그인, 소셜 로그인)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(getRole().toAuthority()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // OAuth2User 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    // 계정 상태 관련 메서드
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !isLock; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
