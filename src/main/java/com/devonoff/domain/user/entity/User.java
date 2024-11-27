package com.devonoff.domain.user.entity;

import com.devonoff.type.LoginType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username; // 사용자 닉네임

  @Column(nullable = false, unique = true)
  private String email; // 사용자 이메일


  @Column(nullable = false)
  private String password;

  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private LoginType loginType = LoginType.GENERAL; // 로그인 타입 (GENERAL, GOOGLE, NAVER 등)

  @Builder.Default
  @Column(nullable = true, name = "profile_image_url")
  private String profileImage = null;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now(); // 가입일

  @Builder.Default
  @Column(name = "update_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 날짜

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  public Long getId() {
    return id;
  }
  @Override
  public String getUsername() {
    return username;
  }

  @Override // 계정만료
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override // 계정잠금
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override // 자격증명
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override //활성상태
  public boolean isEnabled() {
    return this.isActive; // 활성 상태
  }
}

