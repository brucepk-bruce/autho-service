package com.foodie.auth_service.service;

import com.foodie.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service pour gérer la création et la validation des tokens JWT
 */
@Service
public class JwtService {

    // Récupère la clé secrète depuis application.yml
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Récupère la durée d'expiration depuis application.yml (24h en millisecondes)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Génère un token JWT pour un utilisateur
     * @param user L'utilisateur pour lequel générer le token
     * @return Le token JWT sous forme de String
     */
    public String generateToken(User user) {
        // Créer les claims (données embarquées dans le token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        // Construire et retourner le token
        return Jwts.builder()
                .setClaims(claims)                              // Ajouter les données
                .setSubject(user.getEmail())                    // Sujet = email
                .setIssuedAt(new Date())                        // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Expiration 24h
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Signer avec la clé secrète
                .compact();                                     // Générer le token
    }

    /**
     * Extrait l'email depuis le token JWT
     * @param token Le token JWT
     * @return L'email de l'utilisateur
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait l'ID utilisateur depuis le token JWT
     * @param token Le token JWT
     * @return L'ID de l'utilisateur
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extrait le rôle depuis le token JWT
     * @param token Le token JWT
     * @return Le rôle de l'utilisateur
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Vérifie si le token est valide (signature correcte et non expiré)
     * @param token Le token JWT à valider
     * @return true si valide, false sinon
     */
    public boolean isTokenValid(String token) {
        try {
            // Parser le token pour vérifier la signature
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token); // Vérifier l'expiration
        } catch (Exception e) {
            return false; // Token invalide
        }
    }

    /**
     * Vérifie si le token est expiré
     * @param token Le token JWT
     * @return true si expiré, false sinon
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token
     * @param token Le token JWT
     * @return La date d'expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Méthode générique pour extraire une information du token
     * @param token Le token JWT
     * @param claimsResolver Fonction pour extraire l'information désirée
     * @return L'information extraite
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les claims (données) du token
     * @param token Le token JWT
     * @return Les claims du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Génère la clé de signature à partir de la clé secrète
     * @return La clé de signature
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}