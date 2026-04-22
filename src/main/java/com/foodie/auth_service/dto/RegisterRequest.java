package com.foodie.auth_service.dto;

import com.foodie.auth_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est requis")
    private String name;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    private String password;

    private String phone;

    @NotNull(message = "Le rôle est requis")
    private Role role;
}