package com.casino.blackjack.Repository;

import java.util.Optional;

import com.casino.blackjack.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

}
