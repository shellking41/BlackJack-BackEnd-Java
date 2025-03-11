package com.casino.blackjack.security.config;


import com.casino.blackjack.Repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;
  private final JwtService jwtService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    // Extract JWT from cookies instead of Authorization header
    String jwt = jwtService.extractTokenFromCookies(request);
    if (jwt == null) {
      return;
    }

    // Find and revoke the token in the database
    var storedToken = tokenRepository.findByToken(jwt).orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
    }

    // Clear authentication cookies
    clearCookie(response, "access_token");
    clearCookie(response, "refresh_token");

    SecurityContextHolder.clearContext();
  }
  private void clearCookie(HttpServletResponse response, String name) {
    Cookie cookie = new Cookie(name, "");
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
