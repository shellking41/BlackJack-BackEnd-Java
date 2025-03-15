package com.casino.blackjack.Event;

import com.casino.blackjack.Model.Card;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class FlipCardEvent extends ApplicationEvent {
	private final String cardsWorth;
	private final Card card;
	private final Long gameStateId;


	public FlipCardEvent(Object source, String cardsWorth, Card card, Long gameStateId) {
		super(source);
		this.cardsWorth = cardsWorth;
		this.card = card;
		this.gameStateId = gameStateId;
	}
}
