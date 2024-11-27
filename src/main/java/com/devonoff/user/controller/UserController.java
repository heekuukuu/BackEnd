package com.devonoff.user.controller;

import com.devonoff.exception.CustomException;
import com.devonoff.type.ErrorCode;
import com.devonoff.user.dto.CustomUserDetails;
import com.devonoff.user.dto.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


  /**
   * 회원 정보 조회
   */
  @GetMapping("/detail")
  public UserDetailResponse getUserDetails(@AuthenticationPrincipal CustomUserDetails userDetails) {

    if (userDetails == null) {
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    // 사용자 정보 응답 DTO 생성 및 반환
    return UserDetailResponse.builder()
        .id(userDetails.getId())
        .username(userDetails.getUsername())
        .email(userDetails.getEmail())
        .profileImageUrl(userDetails.getProfileImage())
        .build();
  }
}