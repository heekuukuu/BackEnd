package com.devonoff.token.service;

import com.devonoff.exception.CustomException;
import com.devonoff.token.repository.TokenRepository;
import com.devonoff.token.util.JwtTokenProvider;
import com.devonoff.type.ErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final TokenRepository tokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public String refreshAccessToken(String oldRefreshToken) {
    // 리프레시 토큰이 만료되었는지 먼저 확인
    if (jwtTokenProvider.isTokenExpired(oldRefreshToken)) {
      throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED); // 401: 리프레시 토큰이 만료되었습니다.
    }

    // 리프레시 토큰에서 사용자 이름 추출
    String username = jwtTokenProvider.getUsernameFromToken(oldRefreshToken);

    // 사용자 이름이 없으면 유효하지 않은 토큰으로 간주
    if (username == null) {
      throw new CustomException(ErrorCode.INVALID_TOKEN); // 401: 유효하지 않은 토큰입니다.
    }

    // Redis에 저장된 리프레시 토큰 검색
    Set<String> storedTokens = tokenRepository.getRefreshToken(username);
    if (storedTokens.isEmpty() || !storedTokens.contains(oldRefreshToken)) {
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN); // 401: 유효하지 않은 리프레시 토큰입니다.
    }

    // 새롭게 생성된 액세스 토큰 반환
    return jwtTokenProvider.createAccessToken(username);
  }
}