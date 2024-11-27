package com.devonoff.domain.user.controller;

import com.devonoff.domain.user.dto.UserDto;
import com.devonoff.domain.user.entity.User;
import com.devonoff.domain.user.repository.UserRepository;
import com.devonoff.domain.user.service.UserService;
import com.devonoff.exception.CustomException;
import com.devonoff.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;

  /**
   * 로그인된 사용자 정보 조회
   *
   * @return UserDto
   */
  @GetMapping("/detail")
  public ResponseEntity<UserDto> getLoggedInUserDetails() {
    try {
      Long userId = getCurrentUserId(); // 현재 인증된 사용자 ID 가져오기
      UserDto userDetails = userService.getUserDetails(userId);
      return ResponseEntity.ok(userDetails);
    } catch (CustomException e) {
      throw new CustomException(ErrorCode.USER_NOT_FOUND, "인증된 사용자를 찾을 수 없습니다.");
    }
  }

  private Long getCurrentUserId() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof User) {
      return ((User) principal).getId();
    } else if (principal instanceof UserDetails) {
      // principal이 UserDetails 타입일 경우
      String username = ((UserDetails) principal).getUsername();
      return userRepository.findByUsername(username)
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
          .getId();
    }
    throw new CustomException(ErrorCode.USER_NOT_FOUND, "인증된 사용자를 찾을 수 없습니다.");
  }
}

