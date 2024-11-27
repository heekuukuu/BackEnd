package com.devonoff.user.controller;

import com.devonoff.exception.CustomException;
import com.devonoff.token.repository.TokenRepository;
import com.devonoff.token.service.TokenService;
import com.devonoff.token.util.JwtTokenProvider;
import com.devonoff.type.ErrorCode;
import com.devonoff.user.dto.LoginRequest;
import com.devonoff.user.dto.SignUpRequest;
import com.devonoff.user.entity.User;
import com.devonoff.user.repository.UserRepository;
import com.devonoff.user.service.UserService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;


  /**
   * 회원가입
   */
  @PostMapping("/sign-up")
  public ResponseEntity<Map<String, Object>> signUp(@RequestBody SignUpRequest request) {
    userService.signUp(request);
    // 성공 응답 생성
    Map<String, Object> response = new HashMap<>();
    response.put("message", "회원가입에 성공하였습니다.");

    return ResponseEntity.status(201).body(response);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
    // 요청에서 email 키를 가져옴
    String email = request.get("email");

    // 이메일이 데이터베이스에 존재하는지 확인
    boolean exists = userRepository.existsByEmail(email);

    // 응답 메시지 생성
    Map<String, String> response = new HashMap<>();
    if (exists) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED); // 400 반환
    } else {
      response.put("message", "사용가능한 이메일입니다.");
      return ResponseEntity.ok(response); // 200 OK 반환
    }
  }

  @PostMapping("/sign-in/email")
  public ResponseEntity<Map<String, String>> emailLogin(@RequestBody LoginRequest request) {
    // 이메일로 사용자 검색
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS, "존재하지 않는 이메일입니다."));
    // 비밀번호 검증
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.INVALID_CREDENTIALS, "비밀번호가 일치하지 않습니다.");
    }

    // Access Token 및 Refresh Token 생성
    String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());

    // Refresh Token을 Redis에 저장 (유효기간: 7일)
    tokenRepository.saveRefreshToken(user.getUsername(), refreshToken, 7 * 24 * 60 * 60);

    // Access Token만 반환하도록 Map 생성
    Map<String, String> response = new HashMap<>();
    response.put("accessToken", accessToken);

    return ResponseEntity.ok(response);
  }


  /**
   * Access Token을 사용한 새로운 Access Token 갱신
   */
  @PostMapping("/reissue")
  public ResponseEntity<Map<String, String>> reissueToken(
      @RequestHeader("Authorization") String bearerToken) {
    // Bearer 토큰에서 Access Token 추출
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED, "Access Token이 제공되지 않았습니다.");
    }

    String accessToken = bearerToken.substring(7); // "Bearer " 이후의 토큰 값 추출

    // Access Token의 유효성 검증
    if (jwtTokenProvider.isTokenExpired(accessToken)) {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN, "Access Token이 만료되었습니다.");
    }

    String username = jwtTokenProvider.getUsernameFromToken(accessToken);
    if (username == null) {
      throw new CustomException(ErrorCode.INVALID_TOKEN, "유효하지 않은 Access Token입니다.");
    }

    // 새로운 Access Token 생성
    String newAccessToken = jwtTokenProvider.createAccessToken(username);

    // 응답 구성
    Map<String, String> response = new HashMap<>();
    response.put("accessToken", newAccessToken);
    return ResponseEntity.ok(response);
  }
  }