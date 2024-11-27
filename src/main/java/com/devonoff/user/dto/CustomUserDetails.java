package com.devonoff.user.dto;

import com.devonoff.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
public class CustomUserDetails implements UserDetails {

  private final User user;

  // 생성자
  public CustomUserDetails(User user) {
    this.user = user;
  }


  @Override
  public String getPassword() {
    return user.getPassword(); // User 엔티티의 비밀번호
  }

  public String getEmail() {
    return user.getEmail();
  }

  public String getProfileImage() {
    return user.getProfileImage(); // 프로필 이미지 반환
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isEnabled() {
    // 계정이 활성 상태인지 확인
    return user.getIsActive();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 권한 관리 로직을 추가하려면 여기에 추가
    return Collections.emptyList();
  }

  @Override
  public boolean isAccountNonExpired() {
    // 계정이 만료되지 않았는지 확인 (현재는 항상 true)
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // 계정이 잠겨 있지 않았는지 확인
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // 자격 증명이 만료되지 않았는지 확인
    return true;
  }

}