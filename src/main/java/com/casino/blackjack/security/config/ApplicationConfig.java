package com.casino.blackjack.security.config;


import com.casino.blackjack.security.auditing.ApplicationAuditAware;
import com.casino.blackjack.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//Felhasználó keresése az adatbázisból (UserDetailsService).
//Hitelesítés beállítása (authenticationProvider()).
//Automatikus naplózás támogatása (auditorAware()).
//Hitelesítési menedzser konfigurálása (authenticationManager()).
//Jelszó titkosítás beállítása (passwordEncoder()).
//🔹 Miért fontos ez?
//
//Ez az osztály központi szerepet játszik a felhasználók hitelesítésében és az auditorálásban.
//Minden egyes bejelentkezéskor és műveletnél ezek a beállítások biztosítják, hogy az adatok védettek és naplózottak legyenek.

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UserRepository repository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> repository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
//Létrehoz egy DaoAuthenticationProvider objektumot.
//Beállítja a UserDetailsService-t, hogy a rendszer tudja, honnan kell lekérdezni a felhasználókat.
//Megadja a jelszó titkosítót (passwordEncoder()).
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuditorAware<Integer> auditorAware() {
    return new ApplicationAuditAware();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
