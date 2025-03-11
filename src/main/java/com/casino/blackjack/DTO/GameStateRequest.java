package com.casino.blackjack.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor

public class GameStateRequest {
    @NotNull(message = "Bet must not be null")
    @DecimalMin(value = "0.01", message = "Bet must be greater than zero")
    private BigDecimal currentBet;
}
