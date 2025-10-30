package com.crm.authservice.auth_api1.Repository;


import com.crm.authservice.auth_api1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
    boolean existsByFirstnameAndLastname(String firstname, String lastname);
    @Query("SELECT u FROM User u WHERE CONCAT(u.firstname, ' ', u.lastname) = :fullName")
    Optional<User> findByFullName(String fullName);
    Optional<User> findById(Long userId);
}

