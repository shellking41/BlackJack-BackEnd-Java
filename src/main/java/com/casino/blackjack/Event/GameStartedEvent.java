package com.casino.blackjack.Event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.Clock;

@Getter
//ha eventet akarunk akkor annak a applicationEventet kell extendelnie, a source ba a this kell tenni,
//azert kell a event mert igy a kodunk losse coupled es kessobb konyebb tesztelni es asycal mukodhet
//akkor eri meg ha mas classban van a eventlisteneres methodunk
public class GameStartedEvent extends ApplicationEvent {
	private final Integer userId;
	private final BigDecimal currentBet;


	public GameStartedEvent(Object source, Integer userId, BigDecimal currentBet) {
		super(source);
		this.userId = userId;
		this.currentBet = currentBet;
	}
}
