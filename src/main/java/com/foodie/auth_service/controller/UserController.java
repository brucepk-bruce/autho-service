package com.foodie.auth_service.controller;

import com.foodie.auth_service.dto.UserDTO;
import com.foodie.auth_service.model.Role;
import com.foodie.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 *
 * Endpoints disponibles :
 * - GET    /users           : Récupérer tous les utilisateurs
 * - GET    /users/{id}      : Récupérer un utilisateur par ID
 * - GET    /users/role/{role} : Récupérer les utilisateurs par rôle
 * - DELETE /users/{id}      : Supprimer un utilisateur
 *
 * Ces endpoints nécessitent d'être authentifié (token JWT requis)
 * Dans la Phase 2, on ajoutera la vérification du token
 */
@RestController // Indique que c'est un contrôleur REST
@RequestMapping("/api/users") // Tous les endpoints commencent par /users
@RequiredArgsConstructor // Lombok génère le constructeur
@CrossOrigin(origins = "*") // Permet les requêtes depuis Angular
public class UserController {

    // Service métier des utilisateurs
    private final UserService userService;

    /**
     * Retourne la liste de tous les utilisateurs.
     *
     * Réponse (200 OK) :
     * [
     *   {
     *     "id": 1,
     *     "name": "Jean Dupont",
     *     "email": "jean@example.com",
     *     "role": "CLIENT",
     *     ...
     *   },
     *   {
     *     "id": 2,
     *     "name": "Marie Martin",
     *     "email": "marie@example.com",
     *     "role": "RESTAURATEUR",
     *     ...
     *   }
     * ]
     *
     * @return Liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // Récupère tous les utilisateurs
        List<UserDTO> users = userService.getAllUsers();

        // Réponse HTTP 200 avec la liste
        return ResponseEntity.ok(users);
    }

    /**
     * Retourne un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return utilisateur trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        // Récupère l'utilisateur demandé
        UserDTO user = userService.getUserById(id);

        // Réponse HTTP 200 avec l'utilisateur
        return ResponseEntity.ok(user);
    }

    /**
     * Retourne les utilisateurs filtrés par rôle.
     *
     * @param role rôle demandé
     * @return liste des utilisateurs correspondant au rôle
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        // Récupère les utilisateurs du rôle demandé
        List<UserDTO> users = userService.getUsersByRole(role);

        // Réponse HTTP 200 avec la liste
        return ResponseEntity.ok(users);
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return réponse vide avec le statut 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Supprime l'utilisateur demandé
        userService.deleteUser(id);

        // Réponse HTTP 204 sans contenu
        return ResponseEntity.noContent().build();
    }
}