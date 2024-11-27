package com.devonoff.token.util;

import com.devonoff.exception.CustomException;
import com.devonoff.type.ErrorCode;
import com.devonoff.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService userDetailsService;

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
      String username = jwtTokenProvider.getUsernameFromToken(token);

      //유효하지않은 토큰
      if (username == null) {
        throw new CustomException(ErrorCode.INVALID_TOKEN);
      }

      var userDetails = userDetailsService.loadUserByUsername(username);
      //인증객체 생성
      var authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities()
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (CustomException e) {
      // 에러 로깅 및 예외 처리
      request.setAttribute("exception", e.getErrorCode());
      // JwtAuthenticationFilter 내부
      System.out.println("Token: " + token);
      System.out.println("Exception: " + e.getErrorCode());
    }

    chain.doFilter(request, response);
  }
}
