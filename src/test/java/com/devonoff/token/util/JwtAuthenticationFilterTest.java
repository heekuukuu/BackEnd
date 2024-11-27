package com.devonoff.token.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devonoff.user.dto.CustomUserDetails;
import com.devonoff.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class JwtAuthenticationFilterTest {

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private CustomUserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext(); // 테스트 후 SecurityContext 초기화
  }

  @Test  // 유효한 토큰시 인증정보가 설정되는지 검증
  void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
    // Given
    String token = "valid-token";
    String username = "testUser";
    UserDetails userDetails = mock(CustomUserDetails.class);

    when(jwtTokenProvider.resolveToken(request)).thenReturn(token);
    when(jwtTokenProvider.isTokenExpired(token)).thenReturn(false);
    when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain, times(1)).doFilter(request, response);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }



}