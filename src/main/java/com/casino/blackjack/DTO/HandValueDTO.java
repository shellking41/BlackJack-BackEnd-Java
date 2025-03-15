package com.casino.blackjack.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HandValueDTO {
    int highValue;
    int lowValue;

    private boolean isSingleValue(){
        return highValue==lowValue;
    }

    @Override
    public String toString() {
       return isSingleValue()?String.valueOf(lowValue): highValue + "/" + lowValue;
    }
}
