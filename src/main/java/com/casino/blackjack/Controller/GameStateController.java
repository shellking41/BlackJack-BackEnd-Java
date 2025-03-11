package com.casino.blackjack.Controller;

import com.casino.blackjack.DTO.GameStateRequest;
import com.casino.blackjack.DTO.GameStateResponse;
import com.casino.blackjack.Model.GameState;
import com.casino.blackjack.Service.GameStateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/GameState")
public class GameStateController {

private final GameStateService gameStateService;
@PostMapping("/StartGame")
    public GameStateResponse StartGame(@Valid @RequestBody GameStateRequest gameStateRequest){
        return gameStateService.StartGame(gameStateRequest);
    }
@PutMapping("/Stand")
    public GameStateResponse Stand(){
    return gameStateService.Stand();
}

}
