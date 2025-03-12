package com.casino.blackjack.Service;

import com.casino.blackjack.DTO.GameStateRequest;
import com.casino.blackjack.DTO.GameStateResponse;
import com.casino.blackjack.Event.DrawCardEvent;
import com.casino.blackjack.Event.GameStartedEvent;
import com.casino.blackjack.Exception.GameStateNotFoundException;
import com.casino.blackjack.Exception.UserNotFoundException;
import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Model.Enums.GameStatus;
import com.casino.blackjack.Model.GameState;
import com.casino.blackjack.Model.User;
import com.casino.blackjack.Repository.GameStateRepository;
import com.casino.blackjack.Repository.UserRepository;
import com.casino.blackjack.security.auth.AuthenticationService;
import com.casino.blackjack.security.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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
    public GameStateResponse StartGame(GameStateRequest gameStateRequest){

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
    public GameStateResponse Stand(){
        User user = authenticationService.getAuthenticatedUser();
        GameState gameState = user.getGameState();

        if (gameState == null) {
            throw new GameStateNotFoundException("Game does not exist for this user.");
        }

        if (gameState.getIsGameOver()) {
            throw new IllegalStateException("Game is already over.");
        }

        gameState.setStand(true);
        gameStateRepository.save(gameState);
	    return GameStateResponse
	            .builder()
	            .isGameOver(gameState.getIsGameOver())
	            .status(gameState.getStatus())
	            .stand(gameState.isStand())
	            .currentBet(gameState.getCurrentBet())
	            .build();
    }


    @EventListener
    @Transactional
    //itt ossze adjuk a kartya erteket a gamestateba talalhato ertekkel
    protected void updateGameStateWithCard(DrawCardEvent event){

        GameState gameState=gameStateRepository.findById(event.getGameStateId())
                .orElseThrow(()->new GameStateNotFoundException("Game not found"));

        Card card=event.getCard();
        String cardsWorth=event.getCardsWorth();
        int worth = Integer.parseInt(cardsWorth);
        int cardValue=Integer.parseInt(countCardValue(card));


        gameState.getCards().add(card);
        List<Card> cards=gameState.getCards();

        if (card.getCardType() == CardType.PLAYER) {
            gameState.setPlayerCardsWorth(String.valueOf(worth + cardValue));
        } else if (card.getCardType() == CardType.DEALER) {
            gameState.setDealerCardsWorth(String.valueOf(worth + cardValue));
        }

         gameStateRepository.save(gameState);
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
}