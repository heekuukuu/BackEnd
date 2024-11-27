package com.devonoff.domain.token.entity;


import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Token {

  private final String accessToken;
  private final String refreshToken;
  private final Instant accessTokenExpiresAt;
  private final Instant refreshTokenExpiresAt;
}