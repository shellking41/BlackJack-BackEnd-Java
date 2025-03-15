package com.casino.blackjack.Model.Game;

import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Core game logic for Blackjack
 * Encapsulates all the rules and calculations for the Blackjack game
 */


public class BlackJackGameEngine {

    public HandValue countCardValue(GameState gameState, CardType cardType){
        List<Card> cards=gameState.getCards().stream()
                .filter(c->c.getFlipped() && c.getCardType()==cardType)
                .toList();

        int total=0;
        int acesCount=0;

        for (Card c: cards){
            if(c.getValue().equals("A")){
                acesCount+=1;
            }
            else if(Set.of("J","Q","K").contains(c.getValue())){
                total+=10;
            }
            else{
                total+=Integer.parseInt(c.getValue());
            }
        }

        int lowValue=total+acesCount;
        int highValue=lowValue+(acesCount>0?10:0);


        if (highValue > 21) {
            highValue = lowValue;
        }
        return new HandValue(lowValue,highValue);
    }
}
