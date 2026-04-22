package com.foodie.auth_service.service;

import com.foodie.auth_service.dto.*;
import com.foodie.auth_service.model.PasswordResetToken;
import com.foodie.auth_service.model.User;
import com.foodie.auth_service.repository.PasswordResetTokenRepository;  // ✅ AJOUTÉ
import com.foodie.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service pour gérer l'authentification (inscription et connexion)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;  // ✅ AJOUTÉ
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    /**
     * Inscription d'un nouvel utilisateur
     */
    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Connexion d'un utilisateur
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe invalide"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email ou mot de passe invalide");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .user(convertToDTO(user))
                .expiresIn(86400)
                .build();
    }

    /**
     * Demande de réinitialisation de mot de passe
     */
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte associé à cet email"));

        // Supprimer les anciens tokens de cet utilisateur
        passwordResetTokenRepository.deleteByUser(user);

        // Générer un nouveau token unique
        String token = UUID.randomUUID().toString();

        // Créer le token avec expiration de 1 heure
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        // Envoyer l'email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    /**
     * Réinitialisation du mot de passe avec le token
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // Vérifier si le token a expiré
        if (resetToken.isExpired()) {
            throw new RuntimeException("Le token a expiré. Veuillez demander un nouveau lien.");
        }

        // Vérifier si le token a déjà été utilisé
        if (resetToken.isUsed()) {
            throw new RuntimeException("Ce token a déjà été utilisé.");
        }

        // Récupérer l'utilisateur
        User user = resetToken.getUser();

        // Changer le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    /**
     * Convertit une entité User en UserDTO (sans le password)
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}