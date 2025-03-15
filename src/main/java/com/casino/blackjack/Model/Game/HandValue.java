package com.casino.blackjack.Model.Game;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandValue {
    int highValue;
    int lowValue;

    private boolean isSingleValue(){
        return highValue==lowValue;
    }

    @Override
    public String toString() {
        return isSingleValue()?String.valueOf(lowValue): highValue + "/" + lowValue;
    }

    public int getBestValue() {
        return highValue <= 21 ? highValue : lowValue;
    }

    public static HandValue parseHandValue(String handValue) {
        if (handValue.contains("/")) {
            String[] values = handValue.split("/");
            return new HandValue(Integer.parseInt(values[1]), Integer.parseInt(values[0]));
        }
        int value = Integer.parseInt(handValue);
        return new HandValue(value, value);
    }
}
