package com.devonoff.domain.token.repository;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepository {


  @Qualifier("redisTemplate")
  private final RedisTemplate<String, String> redisTemplate;

  /**
   * 리프레시 토큰 저장
   *
   * @param username         사용자 이름 (키)
   * @param refreshToken     리프레시 토큰 (값)
   * @param timeoutInSeconds 토큰 만료 시간 (초 단위)
   */
  public void saveRefreshToken(String username, String refreshToken, long timeoutInSeconds) {
    // 고유 키 생성
    String uniqueKey = username + ":" + UUID.randomUUID();
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(username, refreshToken, timeoutInSeconds, TimeUnit.SECONDS);
  }

  /**
   * 특정사용자 리프레시 토큰 검색
   *
   * @param username 사용자 이름
   * @return 저장된 리프레시 토큰
   */
  public Set<String> getRefreshToken(String username) {
    String pattern = username + ":*"; // 패턴 검색 (username:UUID)
    Set<String> keys = redisTemplate.keys(pattern);
    if (keys == null || keys.isEmpty()) {
      return Set.of(); // 키가 없으면 빈 리스트 반환
    }
    return keys.stream()
        .map(key -> redisTemplate.opsForValue().get(key)) // 각 키에 대해 값(토큰) 가져오기
        .collect(Collectors.toSet());
  }

  /**
   * 특정 사용자의 특정 리프레시 토큰 삭제
   *
   * @param key Redis 저장 키 (username:UUID)
   */
  public void deleteRefreshToken(String key) {
    redisTemplate.delete(key);
  }

  /**
   * 특정 사용자의 모든 리프레시 토큰 삭제
   *
   * @param username 사용자 이름
   */
  public void deleteAllRefreshTokens(String username) {
    String pattern = username + ":*"; // 패턴 검색 (username:UUID)
    Set<String> keys = redisTemplate.keys(pattern);
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys); // 관련 키 전체 삭제
    }
  }
}