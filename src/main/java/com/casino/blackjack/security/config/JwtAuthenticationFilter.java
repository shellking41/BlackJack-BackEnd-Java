package com.casino.blackjack.security.config;


import com.casino.blackjack.Repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
//Bejövő kérések esetén ellenőrzi a JWT tokent.
//Ha érvényes, bejelentkezteti a felhasználót a Spring Security rendszerbe.
//Ha nincs token vagy érvénytelen, egyszerűen továbbengedi a kérést (nem dob hibát, de a védett végpontok eléréséhez szükség lesz bejelentkezésre).
//Nem ellenőrzi az /api/v1/auth végpontokat, így a bejelentkezés és regisztráció működhet token nélkül.
//Ez a szűrő biztosítja, hogy a felhasználók az érvényes JWT tokenjeikkel hozzáférjenek a védett végpontokhoz az alkalmazásodban.
@Component
@RequiredArgsConstructor

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;


  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Extract JWT from cookies instead of Authorization header
    final String jwt = jwtService.extractTokenFromCookies(request);

    if (jwt == null) {
      filterChain.doFilter(request, response);
      return;
    }

    final String userEmail = jwtService.extractUsername(jwt);

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      var isTokenValid = tokenRepository.findByToken(jwt)
              .map(t -> !t.isExpired() && !t.isRevoked())
              .orElse(false);
//ezt nem ertem mert a jwtbol szedi ki a usernevet es a userDetailsbol is, és azt nem ertem ,hogy a userdetails a jwtből tolti be a usernevet akkor minek osszehasonlitani a kettot
      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                //Létrehoz egy WebAuthenticationDetails objektumot, amely tartalmazza a felhasználó böngészőjéből származó adatokat.
                //Például:
                //IP-cím (request.getRemoteAddr())
                //Session ID (request.getRequestedSessionId())
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        //ekéri a Spring Security aktuális hitelesítési környezetét (SecurityContext).
        //setAuthentication(authToken)
        //Beállítja a felhasználó hitelesítési adatait, vagyis azt mondja a rendszernek:
        //✅ „Ez a felhasználó be van jelentkezve”
        //🛠 Miért fontos ez?
        //Mostantól a felhasználó hivatalosan hitelesített a rendszerben.
        //A védett végpontok (@PreAuthorize, @Secured) most már engedélyezhetik a kéréseit.
        //A rendszer nem kéri újra a bejelentkezést minden egyes kérésnél.
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }


}


