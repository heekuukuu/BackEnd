package com.devonoff.token.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.devonoff.exception.CustomException;
import com.devonoff.token.repository.TokenRepository;
import com.devonoff.token.util.JwtTokenProvider;
import com.devonoff.type.ErrorCode;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


  @SpringBootTest
  public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 유효하지 않은 리프레시 토큰 테스트
     */
    @Test
    void 유효하지_않은_리프레시_토큰_테스트() {
      // 준비
      String invalidToken = "invalid-refresh-token";

      // Mock 동작 정의
      Mockito.when(jwtTokenProvider.getUsernameFromToken(invalidToken)).thenReturn(null);

      // 실행 및 검증
      CustomException exception = assertThrows(CustomException.class,
          () -> tokenService.refreshAccessToken(invalidToken));

      assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    /**
     * 만료된 리프레시 토큰 테스트
     */
    @Test
    void 만료된_리프레시_토큰_테스트() {
      // 준비
      String expiredToken = "expired-refresh-token";
      String username = "testUser";

      // Mock 동작 정의
      Mockito.when(jwtTokenProvider.getUsernameFromToken(expiredToken)).thenReturn(username);
      Mockito.when(jwtTokenProvider.isTokenExpired(expiredToken)).thenReturn(true);

      // 실행 및 검증
      CustomException exception = assertThrows(CustomException.class,
          () -> tokenService.refreshAccessToken(expiredToken));

      assertEquals(ErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    /**
     * 정상적인 리프레시 토큰 갱신 테스트
     */
    @Test
    void 정상적인_리프레시_토큰_갱신_테스트() {
      // 준비
      String validToken = "valid-refresh-token";
      String username = "testUser";
      String newAccessToken = "new-access-token";

      // Mock 동작 정의
      Mockito.when(jwtTokenProvider.getUsernameFromToken(validToken)).thenReturn(username);
      Mockito.when(jwtTokenProvider.isTokenExpired(validToken)).thenReturn(false);
      Mockito.when(tokenRepository.getRefreshToken(username)).thenReturn(Set.of(validToken));
      Mockito.when(jwtTokenProvider.createAccessToken(username)).thenReturn(newAccessToken);

      // 실행
      String result = tokenService.refreshAccessToken(validToken);

      // 검증
      assertEquals(newAccessToken, result);
    }
  }
