package com.foodie.auth_service.repository;

import com.foodie.auth_service.model.PasswordResetToken;  // ✅ CHANGÉ
import com.foodie.auth_service.model.User;  // ✅ CHANGÉ
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}