package com.foodie.auth_service.controller;

import com.foodie.auth_service.dto.*;
import com.foodie.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour gérer l'authentification des utilisateurs
 *
 * Endpoints disponibles :
 * - POST /auth/register : Créer un nouveau compte
 * - POST /auth/login    : Se connecter et obtenir un token JWT
 *
 * Tous ces endpoints sont PUBLICS (pas besoin d'être connecté)
 */
@RestController // Indique que c'est un contrôleur REST (retourne du JSON)
@RequestMapping("/api/auth") // Tous les endpoints commencent par /auth
@RequiredArgsConstructor // Lombok génère le constructeur avec authService
@CrossOrigin(origins = "*") // Permet les requêtes depuis n'importe quelle origine (Angular)
public class AuthController {

    // Injection automatique du service d'authentification
    private final AuthService authService;

    /**
     * ENDPOINT : Inscription d'un nouvel utilisateur
     *
     * Méthode HTTP : POST
     * URL : http://localhost:8081/auth/register
     *
     * Body JSON attendu :
     * {
     *   "name": "Jean Dupont",
     *   "email": "jean@example.com",
     *   "password": "password123",
     *   "phone": "0612345678",
     *   "role": "CLIENT"
     * }
     *
     * Réponse en cas de succès (201 Created) :
     * {
     *   "id": 1,
     *   "name": "Jean Dupont",
     *   "email": "jean@example.com",
     *   "phone": "0612345678",
     *   "role": "CLIENT",
     *   "createdAt": "2025-01-15T10:30:00"
     * }
     *
     * @param request Les données d'inscription (validées automatiquement)
     * @return ResponseEntity avec le UserDTO créé et status 201
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        // @Valid active la validation des champs (email, password requis, etc.)
        // @RequestBody convertit le JSON reçu en objet RegisterRequest

        // Appeler le service pour créer l'utilisateur
        UserDTO userDTO = authService.register(request);

        // Retourner une réponse HTTP 201 (Created) avec l'utilisateur créé
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    /**
     * ENDPOINT : Connexion d'un utilisateur
     *
     * Méthode HTTP : POST
     * URL : http://localhost:8081/auth/login
     *
     * Body JSON attendu :
     * {
     *   "email": "jean@example.com",
     *   "password": "password123"
     * }
     *
     * Réponse en cas de succès (200 OK) :
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "user": {
     *     "id": 1,
     *     "name": "Jean Dupont",
     *     "email": "jean@example.com",
     *     "role": "CLIENT"
     *   },
     *   "expiresIn": 86400
     * }
     *
     * Le token JWT doit être stocké par Angular dans localStorage
     * et envoyé dans le header Authorization pour les requêtes suivantes
     *
     * @param request Les identifiants de connexion
     * @return ResponseEntity avec le token JWT et les infos utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // @Valid valide que email et password sont présents
        // @RequestBody convertit le JSON en objet LoginRequest

        // Appeler le service pour vérifier les credentials et générer le token
        LoginResponse response = authService.login(request);

        // Retourner une réponse HTTP 200 (OK) avec le token et user
        return ResponseEntity.ok(response);
    }

    // ... endpoints existants (login, register) ...

    /**
     * Endpoint : Demande de réinitialisation de mot de passe
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request.getEmail());
            return ResponseEntity.ok().body(new MessageResponse("Email de réinitialisation envoyé"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint : Réinitialiser le mot de passe
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok().body(new MessageResponse("Mot de passe réinitialisé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}