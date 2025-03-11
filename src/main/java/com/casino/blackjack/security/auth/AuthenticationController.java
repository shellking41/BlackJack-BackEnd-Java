package com.casino.blackjack.security.auth;

import com.casino.blackjack.DTO.AuthenticationRequest;
import com.casino.blackjack.DTO.RegisterRequest;
import com.casino.blackjack.security.config.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final LogoutService logoutService;

  @PostMapping("/register")
  public ResponseEntity<Map<String,Object>> register(
      @Valid @RequestBody RegisterRequest request,
      HttpServletResponse response
  ) {
    return ResponseEntity.ok(authenticationService.register(request,response));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<Map<String,Object>> authenticate(
     @Valid @RequestBody AuthenticationRequest request,
       HttpServletResponse response
  ) {
    return ResponseEntity.ok(authenticationService.authenticate(request,response));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    authenticationService.refreshToken(request, response);
  }
 @PutMapping("/logout")
  public void logout(
         HttpServletRequest request,
         HttpServletResponse response,
         Authentication authentication
 ){
   logoutService.logout(request,response,authentication);
 }

}
