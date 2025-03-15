package com.casino.blackjack.DTO;

import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.Enums.Symbol;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {

	private Long id;
	@Enumerated(EnumType.STRING)
	private Symbol symbol;
	private String value;
	private Boolean flipped;
	@Enumerated(EnumType.STRING)
	private CardType cardType;



	private Map<String,Object> worth;
}
