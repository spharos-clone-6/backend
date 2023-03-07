package com.ghbt.ghbt_starbucks.auth.controller;

import com.ghbt.ghbt_starbucks.auth.service.AuthService;
import com.ghbt.ghbt_starbucks.security.dto.LoginDto;
import com.ghbt.ghbt_starbucks.security.dto.SignupDto;
import com.ghbt.ghbt_starbucks.security.dto.TokenDto;
import com.ghbt.ghbt_starbucks.user.service.UserServiceImpl;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserServiceImpl userServiceImpl;
  private final BCryptPasswordEncoder encoder;

  private final long COOKIE_EXPIRATION = 90 * 24 * 60 * 60l;

  /**
   * 회원가입
   */
  @PostMapping("/signup")
  public ResponseEntity<Void> signup(@RequestBody @Valid SignupDto signupDto) {
    String encodedPassword = encoder.encode(signupDto.getPassword());
    SignupDto signupDtoWithEncodedPassword = SignupDto.encodePassword(signupDto, encodedPassword);
    userServiceImpl.signupUser(signupDtoWithEncodedPassword);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * 로그인
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
    TokenDto tokenDto = authService.login(loginDto);

    HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
        .maxAge(COOKIE_EXPIRATION)
        .httpOnly(true)
        .secure(true)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
        .build();
  }

  @PostMapping("/validate")
  public ResponseEntity validate(@RequestHeader("Authorization") String requestAccessToken) {
    if (!authService.validate(requestAccessToken)) {
      return ResponseEntity.status(HttpStatus.OK).build();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/reissue")
  public ResponseEntity reissue(
      @CookieValue(name = "refresh-token") String refreshToken,
      @RequestHeader(name = "Authorization") String accessToken) {

    TokenDto reissuedTokenDto = authService.reissue(accessToken, refreshToken);
    if (reissuedTokenDto != null) {
      ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
          .maxAge(COOKIE_EXPIRATION)
          .httpOnly(true)
          .secure(true)
          .build();
      return ResponseEntity.status(HttpStatus.OK)
          .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
          .build();
    } else {
      ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
          .maxAge(0)
          .path("/")
          .build();
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
          .build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
    authService.logout(accessToken);
    ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
        .maxAge(0)
        .path("/")
        .build();
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .build();
  }
}