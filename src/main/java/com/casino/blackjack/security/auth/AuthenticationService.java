package com.casino.blackjack.security.auth;


import com.casino.blackjack.DTO.AuthenticationRequest;
import com.casino.blackjack.DTO.RegisterRequest;
import com.casino.blackjack.Exception.UserNotFoundException;

import com.casino.blackjack.security.config.JwtService;
import com.casino.blackjack.Model.Token;
import com.casino.blackjack.Repository.TokenRepository;
import com.casino.blackjack.Model.Enums.TokenType;
import com.casino.blackjack.Model.User;
import com.casino.blackjack.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Validated
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;
  private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
  private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
//HA VALAKI ROLEBA ADMIN IR AKKOR EGYBOL ADMINKENT KEZELI A RENDSZERE ES EZ NAGY BAJ
  public Map<String,Object> register(RegisterRequest request, HttpServletResponse response) {



    var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))

            .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);

    // Set the cookies
    addTokenCookie(response, ACCESS_TOKEN_COOKIE_NAME, jwtToken, (int) (jwtExpiration / 1000));
    addTokenCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) (refreshExpiration / 1000));

    Map<String,Object> res=new HashMap<>();
    res.put("message","Registration successful");
    return res;
  }
  public  Map<String,Object> authenticate(AuthenticationRequest request, HttpServletResponse response) {
    //Megkeresi a megfelelő UserDetailsService implementációt.
    //Lekérdezi az adatbázisból a felhasználót (az email alapján).
    //Összehasonlítja a megadott jelszót az adatbázisban tárolttal.
    //Ha a jelszó helyes, engedélyezi a bejelentkezést.
    //Ha hibás az adatok valamelyike, hibát dob (BadCredentialsException).
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
    var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);

    // Set the cookies
    addTokenCookie(response, ACCESS_TOKEN_COOKIE_NAME, jwtToken, (int) (jwtExpiration / 1000));
    addTokenCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) (refreshExpiration / 1000));

    Map<String,Object> res=new HashMap<>();
    res.put("message","Login was successful");
    return res;
  }


  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }


  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    // Get refresh token from cookie
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return;
    }

    String refreshToken = Arrays.stream(cookies)
            .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);

    if (refreshToken == null) {
      return;
    }

    final String userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        // Set new cookies
        addTokenCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) (jwtExpiration / 1000));
        // Don't need to reset refresh token cookie as it's still valid

        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  private void  addTokenCookie(HttpServletResponse response,String name,String value, int maxAge){
    Cookie cookie=new Cookie(name,value);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);

  }
  public User getAuthenticatedUser() {

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String email;

    if (principal instanceof UserDetails) {
      email = ((UserDetails) principal).getUsername();  // 🔹 E-mail kinyerése
    } else {
      throw new IllegalStateException("Unexpected authentication principal type");
    }

    return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
  }
}
