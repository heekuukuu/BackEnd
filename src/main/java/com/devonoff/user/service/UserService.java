package com.devonoff.user.service;

import com.devonoff.user.dto.SignUpRequest;
import com.devonoff.user.entity.User;
import com.devonoff.user.repository.UserRepository;
import com.devonoff.user.type.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // 회원가입 메서드 구현
  public void signUp(SignUpRequest request) {
    // 이메일 중복 확인
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // User 엔티티 생성 (빌더 패턴 사용)
    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(encodedPassword)
        .loginType(LoginType.GENERAL)
        .isActive(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    // 저장
    userRepository.save(user);
  }



}