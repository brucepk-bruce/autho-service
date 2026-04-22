package com.foodie.auth_service.service;

import com.foodie.auth_service.dto.UserDTO;
import com.foodie.auth_service.model.Role;
import com.foodie.auth_service.model.User;
import com.foodie.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les opérations CRUD sur les utilisateurs
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Récupère tous les utilisateurs
     * @return Liste de tous les utilisateurs
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id L'ID de l'utilisateur
     * @return Les informations de l'utilisateur
     * @throws RuntimeException Si l'utilisateur n'existe pas
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToDTO(user);
    }

    /**
     * Récupère tous les utilisateurs par rôle
     * @param role Le rôle (CLIENT, RESTAURATEUR, LIVREUR)
     * @return Liste des utilisateurs avec ce rôle
     */
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprime un utilisateur
     * @param id L'ID de l'utilisateur à supprimer
     * @throws RuntimeException Si l'utilisateur n'existe pas
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }

    /**
     * Convertit une entité User en UserDTO (sans le password)
     * @param user L'entité User
     * @return Le DTO
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