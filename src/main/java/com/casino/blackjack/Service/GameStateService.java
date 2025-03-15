package com.casino.blackjack.Service;

import com.casino.blackjack.DTO.GameStateRequest;
import com.casino.blackjack.DTO.GameStateResponse;
import com.casino.blackjack.Event.FlipCardEvent;
import com.casino.blackjack.Event.GameStartedEvent;
import com.casino.blackjack.Exception.GameStateNotFoundException;
import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.Enums.GameStatus;
import com.casino.blackjack.Model.Game.HandValue;
import com.casino.blackjack.Model.GameState;
import com.casino.blackjack.Model.User;
import com.casino.blackjack.Repository.GameStateRepository;
import com.casino.blackjack.Repository.UserRepository;
import com.casino.blackjack.security.auth.AuthenticationService;
import com.casino.blackjack.security.config.JwtService;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.casino.blackjack.Model.Game.HandValue.parseHandValue;

@Service
@RequiredArgsConstructor
@Data
public class GameStateService {

    private final GameStateRepository gameStateRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher eventPublisher;
    private final CardService cardService;

    @Transactional
    public GameStateResponse startGame(GameStateRequest gameStateRequest){

        GameState gameState=GameState
                .builder()
                .isGameOver(false)
                .status(GameStatus.Active)
                .currentBet(gameStateRequest.getCurrentBet())
                .build();

        User user=authenticationService.getAuthenticatedUser();

        if(user.getGameState()!=null && !user.getGameState().getIsGameOver()){
            throw new IllegalStateException("Game is already started");
        }
        //itt inditunk el egy eventet es megadhatjuk hogy melyik event induljon el
        eventPublisher.publishEvent(new GameStartedEvent(this,user.getId(),gameStateRequest.getCurrentBet()));

        user.setGameState(gameState);

        // Fontos: Először mentjük a GameState-et, majd frissítjük a User-t
        gameStateRepository.save(gameState);
        userRepository.save(user);

        return GameStateResponse
                .builder()
                .isGameOver(gameState.getIsGameOver())
                .status(gameState.getStatus())
                .stand(gameState.isStand())
                .currentBet(gameState.getCurrentBet())
                .build();
    }
    @Transactional
    public GameStateResponse stand(){
        User user = authenticationService.getAuthenticatedUser();
        GameState gameState = user.getGameState();

        if (gameState == null) {
            throw new GameStateNotFoundException("Game does not exist for this user.");
        }

        if (gameState.getIsGameOver()) {
            throw new IllegalStateException("Game is already over.");
        }
        List<Card> dealerCards = gameState.getCards().stream()
                .filter(card -> card.getCardType().equals(CardType.DEALER))
                .toList();
        if(dealerCards.size()<2){
            throw new IllegalStateException("Dealer has less then 2 cards");
        }

        gameState.setStand(true);
        gameState.setPlayerCardsWorth(String.valueOf(parseHandValue(gameState.getPlayerCardsWorth()).getBestValue()));




        // Dealer kártyáinak automatikus felfordítása
        dealerCards.forEach(card -> {
            if (!card.getFlipped()) {
                card.setFlipped(true);
            }
        });

        gameStateRepository.save(gameState);

        Map<String,Object> updatedValues=new HashMap<>();
        updatedValues.put("playerCardsWorth",gameState.getPlayerCardsWorth());
        updatedValues.put("dealersCard",dealerCards);

	    return GameStateResponse
	            .builder()
	            .isGameOver(gameState.getIsGameOver())
	            .status(gameState.getStatus())
	            .stand(gameState.isStand())
	            .currentBet(gameState.getCurrentBet())
                .updatedValues(updatedValues)
	            .build();
    }

    @EventListener
    @Transactional
    //itt ossze adjuk a kartya erteket a gamestateba talalhato ertekkel
    protected void updateGameStateWithCard(FlipCardEvent event){

        GameState gameState=gameStateRepository.findById(event.getGameStateId())
                .orElseThrow(()->new GameStateNotFoundException("Game not found"));

        Card card=event.getCard();
        CardType cardType=card.getCardType();



        gameState.getCards().add(card);

        if (cardType == CardType.PLAYER) {
            gameState.setPlayerCardsWorth(countCardValue(gameState,cardType).toString());
        } else if (cardType == CardType.DEALER) {
            gameState.setDealerCardsWorth(countCardValue(gameState,cardType).toString());
        }

         gameStateRepository.save(gameState);
    }

    private HandValue countCardValue(GameState gameState, CardType cardType){
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