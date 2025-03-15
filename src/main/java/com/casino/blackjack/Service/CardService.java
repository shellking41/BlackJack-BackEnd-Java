package com.casino.blackjack.Service;

import com.casino.blackjack.DTO.CardRequest;
import com.casino.blackjack.DTO.CardResponse;
import com.casino.blackjack.Event.FlipCardEvent;
import com.casino.blackjack.Exception.CardNotFoundException;
import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.Enums.Symbol;
import com.casino.blackjack.Model.GameState;
import com.casino.blackjack.Repository.CardRepository;
import com.casino.blackjack.security.auth.AuthenticationService;
import com.casino.blackjack.security.config.JwtService;
import com.casino.blackjack.Model.User;
import com.casino.blackjack.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;



    public Map<String, Object> AllCard(){

        User user=authenticationService.getAuthenticatedUser();

        Map<String,Object> response=new HashMap<>();
        Stream<Card> playerCards=user.getGameState().getCards().stream().filter(card -> card.getCardType().equals(CardType.PLAYER));
        Stream<Card> dealerCards=user.getGameState().getCards().stream().filter(card -> card.getCardType().equals(CardType.DEALER));
        response.put("PlayerCards",playerCards);
        response.put("DealerCards",dealerCards);
        return response;
    }
    @Transactional
    public Card DrawCard(CardRequest cardRequest) {
        //itt megszerezzuk a usert es megnezzuk hogy van e létezik e game
        User user=authenticationService.getAuthenticatedUser();
        GameState gameState = user.getGameState();

        if (gameState == null || gameState.getIsGameOver()) {
            throw new IllegalStateException("The game is finished or User has no associated GameState.");
        }
        Card card = RandomCard(gameState,cardRequest.getCardType());
        logger.info("New player card created: {} for user: {}", card, user.getEmail());
        return cardRepository.save(card);
    }

     @Transactional
     public CardResponse flipCard(Long cardId){

         //itt megszerezzuk a usert es megnezzuk hogy van e létezik e game
         User user=authenticationService.getAuthenticatedUser();
         GameState gameState = user.getGameState();
         if (gameState == null || gameState.getIsGameOver()) {
             throw new IllegalStateException("The game is finished or User has no associated GameState.");
         }

         Card card= cardRepository.findById(cardId)
                    .orElseThrow(()-> new CardNotFoundException("Card not found"));

         if(card.getFlipped()){
             throw new IllegalStateException("Card all ready flipped");
         }

         card.setFlipped(true);

         if(!gameState.isStand()) {
             eventPublisher.publishEvent(new FlipCardEvent(this,gameState.getPlayerCardsWorth(),card,gameState.getId()));
         }else{
             eventPublisher.publishEvent(new FlipCardEvent(this,gameState.getDealerCardsWorth(),card,gameState.getId()));
         }



         Map<String,Object> values=new HashMap<>();
         values.put("playerCardsWorth",gameState.getPlayerCardsWorth());
         values.put("dealerCardsWorth",gameState.getDealerCardsWorth());

         return CardResponse.builder()
                 .id(card.getId())
                 .cardType(card.getCardType())
                 .symbol(card.getSymbol())
                 .value(card.getValue())
                 .flipped(card.getFlipped())
                 .worth(values)
                 .build();
     }

    protected Card RandomCard(GameState gameState,CardType cardType){
        Symbol[] symbols = Symbol.values();
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        Random random = new Random();
        Symbol randomSymbol = symbols[random.nextInt(symbols.length)];
        String randomValue = values[random.nextInt(values.length)];
//        CardType cardType = gameState.isStand() ? CardType.DEALER : CardType.PLAYER;

        return Card.builder()
                .value(randomValue)
                .symbol(randomSymbol)
                .flipped(false)
                .cardType(cardType)
                .gameState(gameState)
                .build();
    }


}
