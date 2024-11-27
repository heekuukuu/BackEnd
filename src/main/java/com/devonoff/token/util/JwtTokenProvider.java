package com.devonoff.token.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JwtTokenProvider는 JWT 토큰을 생성하고 관리하는 유틸리티 클래스입니다.
 */
@Component
public class JwtTokenProvider {

  private Key key;

  // 애플리케이션 설정 파일(application.yaml 등)에서 jwt.secret 값을 가져옵니다.
  @Value("${jwt.secret}")
  private String secretKey;

  // Access Token의 유효기간 (1시간)
  private final long accessTokenValidity = 60 * 60 * 1000L;

  // Refresh Token의 유효기간 (7일)
  private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000L;

  /**
   * Spring Bean 초기화 후 Key를 생성합니다.
   */
  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  /**
   * Access Token 생성
   *
   * @param username - 사용자 이름(또는 ID)
   * @return Access Token 문자열
   */
  public String createAccessToken(String username) {
    return createToken(username, accessTokenValidity);
  }

  /**
   * Refresh Token 생성
   *
   * @param username - 사용자 이름(또는 ID)
   * @return Refresh Token 문자열
   */
  public String createRefreshToken(String username) {
    return createToken(username, refreshTokenValidity);
  }

  /**
   * 공통 토큰 생성 메서드
   *
   * @param username - 토큰의 주체가 되는 사용자 이름(또는 ID)
   * @param validity - 토큰의 유효기간 (밀리초)
   * @return 생성된 JWT 토큰 문자열
   */
  private String createToken(String username, long validity) {
    // Claims는 JWT의 payload에 해당하며, 사용자 정보 및 데이터를 담습니다.
    Claims claims = Jwts.claims().setSubject(username);

    // 현재 시간
    Date now = new Date();

    // JWT 생성
    return Jwts.builder()
        .setClaims(claims) // 사용자 정보 설정
        .setIssuedAt(now) // 토큰 발행 시간
        .setExpiration(new Date(now.getTime() + validity)) // 토큰 만료 시간
        .signWith(key) // Key 객체를 사용한 서명
        .compact(); // 최종적으로 JWT 문자열 생성
  }

  /**
   * 토큰에서 사용자 이름 추출
   *
   * @param token - JWT 토큰
   * @return 사용자 이름(주체)
   */
  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  /**
   * 토큰의 만료 여부 확인
   *
   * @param token - JWT 토큰
   * @return 토큰이 만료되었는지 여부 (true: 만료됨)
   */
  public boolean isTokenExpired(String token) {
    Date expiration = getClaimsFromToken(token).getExpiration();
    return expiration.before(new Date());
  }

  /**
   * 토큰에서 Claims 추출
   *
   * @param token - JWT 토큰
   * @return Claims (JWT payload)
   */
  private Claims getClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key) // 서명 검증에 사용되는 Key 설정
        .build()
        .parseClaimsJws(token) // JWT 토큰을 파싱
        .getBody(); // Payload 반환
  }

  /**
   * 요청 헤더에서 토큰 추출
   *
   * @param request HTTP 요청
   * @return 추출된 JWT 토큰 (없으면 null 반환)
   */
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    // Authorization 헤더가 "Bearer "로 시작하면 토큰 추출
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
    }

    return null; // 헤더에 토큰이 없으면 null 반환
  }
}