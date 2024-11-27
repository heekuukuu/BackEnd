package com.devonoff.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.devonoff.domain.user.entity.User;
import com.devonoff.domain.user.repository.UserRepository;
import com.devonoff.domain.user.service.CustomUserDetailsService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

  @InjectMocks
  private CustomUserDetailsService customUserDetailsService;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test // 유저정보 잘가져오는지체크
  void loadUserByUsername_UserExists_ReturnsUserDetails() {
    // Given
    String username = "testUser";
    User mockUser = User.builder()
        .id(1L)
        .username(username)
        .email("test@example.com")
        .password("encryptedPassword")
        .build();
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

    // When
    CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

    // Then
    assertEquals(mockUser.getUsername(), userDetails.getUsername());
    assertEquals(mockUser.getPassword(), userDetails.getPassword());
  }

  @Test // 유효회원아닐지 예외처리여부
  void loadUserByUsername_UserDoesNotExist_ThrowsException() {
    // Given
    String username = "nonexistentUser";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // When & Then
    UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername(username)
    );

    assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
  }
}