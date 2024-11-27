package com.devonoff.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponse {

  private Long id;
  private String username;
  private String email;
  private String profileImageUrl;
}