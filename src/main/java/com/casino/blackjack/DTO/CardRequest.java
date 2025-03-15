package com.casino.blackjack.DTO;

import com.casino.blackjack.Model.Enums.CardType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class CardRequest {

    @NotNull(message = "Type must not be null")
    @Enumerated(EnumType.STRING)
    private CardType cardType;
}
