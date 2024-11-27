package com.devonoff.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {

  private String accessToken;
  private String refreshToken;
}

