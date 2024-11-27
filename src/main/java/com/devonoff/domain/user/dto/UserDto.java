package com.devonoff.domain.user.dto;

import com.devonoff.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
  private Long id;
  private String username;
  private String email;
  private String profileImageUrl;

  //유저조회
  public static UserDto fromEntity(User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .profileImageUrl(user.getProfileImage())
        .build();
  }
}


