package com.casino.blackjack.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Builder
public class Statistic {

    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private BigDecimal RoundMoney=BigDecimal.ZERO;

    private Boolean win=false;
    private Boolean lose=false;
    private Boolean draw=false;

    @Builder.Default
    private BigDecimal placedBet=BigDecimal.ZERO;

    private Date roundDate;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
