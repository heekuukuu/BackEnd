package com.devonoff.token.service;

import com.devonoff.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
  private final TokenRepository tokenRepository;

  public void login(String username, String refreshToken, long refreshTokenValidity) {
    // Redis에 Refresh Token 저장
    tokenRepository.saveRefreshToken(username, refreshToken, refreshTokenValidity);
  }

  public String getRefreshToken(String username) {
    // Redis에서 Refresh Token 검색
    return tokenRepository.getRefreshToken(username);
  }

  public void logout(String username) {
    // Redis에서 Refresh Token 삭제
    tokenRepository.deleteRefreshToken(username);
  }
}