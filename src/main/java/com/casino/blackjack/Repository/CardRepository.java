package com.casino.blackjack.Repository;

import com.casino.blackjack.Model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

}
