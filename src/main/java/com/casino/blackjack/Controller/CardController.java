package com.casino.blackjack.Controller;

import com.casino.blackjack.DTO.CardRequest;
import com.casino.blackjack.DTO.CardResponse;
import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Model.Enums.CardType;
import com.casino.blackjack.Service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/Card")
public class CardController {

    private final CardService cardService;



    @PostMapping("/DrawCard")
    public Card DrawCard(@Valid @RequestBody CardRequest cardRequest) {

        return cardService.DrawCard(cardRequest);
    }

    @GetMapping("/AllCard")
    public Map<String, Object> AllCard(){

        return cardService.AllCard();
    }

    @GetMapping("/test")
    public String test(){


        return "Megyen";
    }

    @PutMapping("/Flip/{cardId}")
    public CardResponse flipCard(@PathVariable Long cardId){
        return cardService.flipCard(cardId);
    }
}
