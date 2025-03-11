package com.casino.blackjack.Service;

import com.casino.blackjack.DTO.GameStateRequest;
import com.casino.blackjack.DTO.GameStateResponse;
import com.casino.blackjack.Event.GameStartedEvent;
import com.casino.blackjack.Exception.UserNotFoundException;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Data
public class GameStateService {

    private final GameStateRepository gameStateRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher eventPublisher;

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
            throw new IllegalStateException("Game does not exist for this user.");
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

}
