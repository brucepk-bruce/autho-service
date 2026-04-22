package com.foodie.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de la sécurité de l'application
 * - Désactive CSRF (pas besoin pour une API REST)
 * - Autorise tous les endpoints pour les tests
 * - Configure le PasswordEncoder (BCrypt)
 * - Configure CORS pour Angular
 */
@Configuration
@EnableWebSecurity // Active la sécurité Spring
public class SecurityConfig {

    /**
     * Bean pour encoder les mots de passe avec BCrypt
     * BCrypt ajoute automatiquement du "salt" pour plus de sécurité
     *
     * @return PasswordEncoder configuré avec BCrypt force 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configuration de la chaîne de filtres de sécurité
     *
     * Pour les tests, on autorise TOUTES les requêtes
     * Plus tard, on ajoutera la validation JWT ici
     *
     * @param http L'objet HttpSecurity pour configurer la sécurité
     * @return La chaîne de filtres configurée
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF (Cross-Site Request Forgery)
                // Pas besoin pour une API REST (on utilise JWT)
                .csrf(csrf -> csrf.disable())

                // Activer CORS (autorise les requêtes depuis Angular)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configuration des autorisations
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // AUTORISER toutes les requêtes (pour les tests)
                )

                // Configurer la session comme STATELESS
                // Pas de session HTTP, on utilise JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    /**
     * Configuration CORS pour autoriser les requêtes depuis Angular
     *
     * @return CorsConfigurationSource configuré
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Autoriser ces origines (Angular dev + prod)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",                      // Angular développement
                "https://foodie-delivery.web.app",            // Angular production Firebase
                "https://foodie-delivery.firebaseapp.com"     // Alternative Firebase
        ));

        // Autoriser ces méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Autoriser tous les headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permettre les credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Appliquer à tous les endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}