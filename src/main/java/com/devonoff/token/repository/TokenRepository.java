package com.devonoff.token.repository;

import java.util.concurrent.TimeUnit;
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

    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(username, refreshToken, timeoutInSeconds, TimeUnit.SECONDS);
  }

  /**
   * 리프레시 토큰 검색
   *
   * @param username 사용자 이름
   * @return 저장된 리프레시 토큰
   */
  public String getRefreshToken(String username) {

    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    return ops.get(username); // 값이 없으면 null 반환
  }

  /**
   * 리프레시 토큰 삭제
   *
   * @param username 사용자 이름 (키)
   */
  public void deleteRefreshToken(String username) {

    redisTemplate.delete(username);
  }
}