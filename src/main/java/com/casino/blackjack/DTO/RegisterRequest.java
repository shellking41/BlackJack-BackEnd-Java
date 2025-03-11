package com.casino.blackjack.DTO;


import com.casino.blackjack.security.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "firstname is required")
  private String firstname;
  @NotBlank(message = "lastname is required")
  private String lastname;
  @NotBlank(message = "email is required")
  private String email;
  @NotBlank(message = "password is required")
  private String password;

}
