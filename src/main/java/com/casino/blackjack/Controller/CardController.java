package com.casino.blackjack.Controller;

import com.casino.blackjack.DTO.CardResponse;
import com.casino.blackjack.Model.Card;
import com.casino.blackjack.Service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/Card")
public class CardController {

    private final CardService cardService;



    @PostMapping("/DrawCard")
    public CardResponse DrawCard() {

        return cardService.DrawCard();
    }

    @GetMapping("/AllCard")
    public Map<String, Object> AllCard(){

        return cardService.AllCard();
    }

    @GetMapping("/test")
    public String test(){


        return "Megyen";
    }
}
