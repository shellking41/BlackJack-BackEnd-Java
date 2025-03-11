package com.casino.blackjack.Repository;

import com.casino.blackjack.Model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStateRepository extends JpaRepository<GameState,Long> {

}
