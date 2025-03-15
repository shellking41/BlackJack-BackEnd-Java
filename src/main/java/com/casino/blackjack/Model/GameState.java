package com.casino.blackjack.Model;

import com.casino.blackjack.Model.Enums.GameStatus;
import com.casino.blackjack.Model.Game.HandValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Builder
public class GameState {

    @Id
    @GeneratedValue
    private Long id;

    private Boolean isGameOver;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private boolean stand;


    private String playerCardsWorth;


    private String dealerCardsWorth;

    @Builder.Default
    private BigDecimal currentBet= BigDecimal.ZERO;

    @OneToMany(mappedBy = "gameState", cascade = CascadeType.ALL)
    private List<Card> Cards=new ArrayList<>();



    @PrePersist
    protected void onCreate(){
        playerCardsWorth = "0";
        dealerCardsWorth = "0";
        stand=false;
    }

}
