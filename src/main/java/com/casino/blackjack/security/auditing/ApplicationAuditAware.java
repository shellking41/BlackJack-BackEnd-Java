package com.casino.blackjack.security.auditing;


import com.casino.blackjack.Model.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
//Lekéri az aktuális felhasználót a Spring Security-ből.
//Ellenőrzi, hogy be van-e jelentkezve.
//Ha nincs bejelentkezve, üres értéket (Optional.empty()) ad vissza.
//Ha be van jelentkezve, visszaadja az ő adatbázis ID-jét (Optional.of(userPrincipal.getId())).
//Ezt az információt a rendszer az adatbázis műveleteknél (pl. ki hozott létre egy rekordot?) automatikusan eltárolhatja.

public class ApplicationAuditAware implements AuditorAware<Integer> {
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        if (authentication == null ||
            !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }


        User userPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());
    }
}
