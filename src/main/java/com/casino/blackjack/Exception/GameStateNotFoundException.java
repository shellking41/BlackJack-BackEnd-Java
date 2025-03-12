package com.casino.blackjack.Exception;

public class GameStateNotFoundException extends RuntimeException {
	public GameStateNotFoundException(String message) {
		super(message);
	}
}
