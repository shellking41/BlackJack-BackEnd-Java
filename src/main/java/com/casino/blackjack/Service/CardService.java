package com.casino.blackjack.Service;

import com.casino.blackjack.DTO.CardResponse;
import com.casino.blackjack.Exception.UserNotFoundException;
import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.Enums.Symbol;
import com.casino.blackjack.Model.GameState;
import com.casino.blackjack.Repository.CardRepository;
import com.casino.blackjack.security.auth.AuthenticationService;
import com.casino.blackjack.security.config.JwtService;
import com.casino.blackjack.Model.User;
import com.casino.blackjack.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Stream;


@Service
@Data
@RequiredArgsConstructor
@Validated
public class CardService {

   private static final Logger logger= LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;



    public Map<String, Object> AllCard(){

        User user=authenticationService.getAuthenticatedUser();

        Map<String,Object> response=new HashMap<>();
        Stream<Card> playerCards=user.getGameState().getPlayerCards().stream().filter(card -> card.getCardType().equals(CardType.PLAYER));
        Stream<Card> dealerCards=user.getGameState().getDealerCards().stream().filter(card -> card.getCardType().equals(CardType.DEALER));
        response.put("PlayerCards",playerCards);
        response.put("DealerCards",dealerCards);
        return response;
    }
    @Transactional
    public CardResponse NewCard() {
        //itt megszerezzuk a usert es megnezzuk hogy van e létezik e game
        User user=authenticationService.getAuthenticatedUser();
        GameState gameState = user.getGameState();
        if (gameState == null || gameState.getIsGameOver()) {
            throw new IllegalStateException("The game is finished or User has no associated GameState.");
        }

        Card card = RandomCard();
        //ha a felhasznalo nem standelt akkor a playerkartyakat huzzon es szamolaj a erteket es ha igen a dealer
        if(!gameState.isStand()) {
            card.setCardType(CardType.PLAYER);
            gameState.setPlayerCardsWorth(updateCardsWorth(gameState.getPlayerCardsWorth(), card));
        }else{
            card.setCardType(CardType.DEALER);
            gameState.setDealerCardsWorth(updateCardsWorth(gameState.getDealerCardsWorth(), card));
        }

        card.setGameState(gameState);
        gameState.getPlayerCards().add(card);
        logger.info("New player card created: {} for user: {}", card, user.getEmail());

        cardRepository.save(card);

        Map<String,Object> values=new HashMap<>();
        values.put("playerCardsWorth",gameState.getPlayerCardsWorth());
        values.put("dealerCardsWorth",gameState.getDealerCardsWorth());

        return CardResponse.builder()
                .cardType(card.getCardType())
                .symbol(card.getSymbol())
                .value(card.getValue())
                .flipped(card.getFlipped())
                .worth(values)
                .build();
    }
    //itt ossze adjuk a kartya erteket a gamestateba talalhato ertekkel
    private String updateCardsWorth(String cardsWorth,Card card){
        int worth = 0;
        try {
            worth = Integer.parseInt(cardsWorth);
        } catch (NumberFormatException e) {
            logger.error("Invalid value for cardsWorth: {}", cardsWorth, e);
        }
        int cardValue=Integer.parseInt(countCardValue(card));
        return String.valueOf(worth+cardValue);
    }

    private String countCardValue(Card card){
        String cardValue=card.getValue();

        Set<String> faceCards=Set.of("J","Q","K");

        if(cardValue.equals("A")){
            return "11";
        }
        else if(faceCards.contains(cardValue)){
            return "10";
        }
        else{
            return cardValue;
        }


    }

    protected Card RandomCard(){
        Symbol[] symbols=Symbol.values();
        String[] values={"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        Random random=new Random();
        Symbol randomSymbol=symbols[random.nextInt(symbols.length)];
        String randomValue=values[random.nextInt(values.length)];

        return Card.builder()
                .value(randomValue)
                .symbol(randomSymbol)
                .flipped(false)
                .build();
    }


}
