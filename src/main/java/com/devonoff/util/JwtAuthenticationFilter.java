package com.devonoff.util;

import com.devonoff.domain.user.entity.User;
import com.devonoff.domain.user.repository.UserRepository;
import com.devonoff.exception.CustomException;
import com.devonoff.type.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    String token = jwtTokenProvider.resolveToken(request);

    try {
      // 토큰이 없을때
      if (token == null) {
        throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED);
      }
      // 토큰 만료
      if (jwtTokenProvider.isTokenExpired(token)) {
        throw new CustomException(ErrorCode.EXPIRED_TOKEN);
      }
      //이름추출
      Long id = jwtTokenProvider.getUserIdFromToken(token);

      //유효하지않은 토큰
      if (id == null) {
        throw new CustomException(ErrorCode.INVALID_TOKEN);
      }

      User user = userRepository.findById(id)
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      //인증객체 생성
      var authentication = new UsernamePasswordAuthenticationToken(
          user, null, Collections.emptyList()
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      System.out.println("Token: " + token);
      System.out.println("Decoded User ID: " + id);
      System.out.println("User found in DB: " + user);
      System.out.println("Authentication object: " + SecurityContextHolder.getContext().getAuthentication());
    } catch (CustomException e) {
      log.error("JWT 인증 실패: {} - {}", e.getErrorCode(), e.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
      return; // 필터 체인을 중단

    }
    chain.doFilter(request, response);
  }
}
