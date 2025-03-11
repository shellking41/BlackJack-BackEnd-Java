package com.casino.blackjack.Exception;

public class InvalidBetException extends RuntimeException {
    public InvalidBetException(String message) {
        super(message);
    }
}
