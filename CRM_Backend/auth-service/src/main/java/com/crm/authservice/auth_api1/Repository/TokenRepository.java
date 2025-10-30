package com.crm.authservice.auth_api1.Repository;

import com.crm.authservice.auth_api1.models.Token;
import com.crm.authservice.auth_api1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);
    void deleteAllByUser(User user);

}
