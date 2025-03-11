package com.casino.blackjack.Service;


import com.casino.blackjack.Event.GameStartedEvent;
import com.casino.blackjack.Model.User;
import com.casino.blackjack.Repository.UserRepository;
import com.casino.blackjack.security.user.ChangePasswordRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final UserRepository userRepository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
//Ez egy már hitelesített felhasználót ad vissza, aki be van jelentkezve a Spring Security rendszerébe.
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    @Transactional
    @EventListener
    //eza  event listener figyel arra hogy elindult e a GameStartedEvent es ha elidult akkor ez lefut
    public void deductTheBet(GameStartedEvent event){
        User user=userRepository.findById(event.getUserId())
                .orElseThrow(()->new IllegalStateException("User not found"));

        BigDecimal money=user.getMoney();
        BigDecimal currentBet=event.getCurrentBet();

        if(money.compareTo(currentBet)<0){
            throw new IllegalStateException("User doesn't have enough money");

        }
        BigDecimal newBalance= money.subtract(currentBet);
        user.setMoney(newBalance);
        userRepository.save(user);

    }
}
