package com.casino.blackjack.DTO;

import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStateResponse {

	private Boolean isGameOver;

	@Enumerated(EnumType.STRING)
	private GameStatus status;

	private boolean stand;

	private Map<String,Object> updatedValues;
	private BigDecimal currentBet;


}
