package com.casino.blackjack.Model;

import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.Enums.Symbol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "gameState")
@Table
@Builder
public class Card {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Symbol symbol;
    private String value;
    private Boolean flipped;
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @ManyToOne
    @JoinColumn(name = "game_state_id",nullable = false)
    @JsonIgnore
    private GameState gameState;


}
