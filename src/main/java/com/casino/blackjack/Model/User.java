package com.casino.blackjack.Model;


import com.casino.blackjack.security.user.Role;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Integer id;
  private String firstname;
  private String lastname;
  private String email;
  private String password;

  private BigDecimal money;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;



  @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL)
  private List<Statistic> statistics;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name="game_state_id",nullable = true, unique = true)
  private GameState gameState;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  //megcsinalja azokat  a dolgokat amiket a adatbazis feltotelse elott kellene csinalni
  @PrePersist
  protected void onCreate(){
    role=Role.USER;
    money= BigDecimal.valueOf(100);
  }
}
