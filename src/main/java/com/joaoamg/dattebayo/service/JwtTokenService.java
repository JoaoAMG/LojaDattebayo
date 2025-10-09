package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Usuario; // Classe base
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
import java.util.UUID;

@Service
public class JwtTokenService {

    // Injeta os valores configurados
    @Value("${jwt.secret}") private String jwtSecret;
    @Value("${jwt.access.expiration}") private long accessExpiration;
    @Value("${jwt.refresh.short-expiration}") private long refreshShortExpiration;
    @Value("${jwt.refresh.long-expiration}") private long refreshLongExpiration;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(UUID userId, String subject, long expirationMs, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 1. Geração do Access Token (Sempre curto)
    public String generateAccessToken(Usuario usuario) {
        // Aqui você adicionaria as ROLES/Autoridades do usuário (SUPER, MODERADOR, etc.)
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("email", usuario.getEmail());

        return buildToken(usuario.getId(), usuario.getEmail(), accessExpiration, claims);
    }

    // 2. Geração do Refresh Token (Duração ajustada)
    public String generateRefreshToken(Usuario usuario, boolean rememberMe) {
        // ✅ Lógica de duração baseada no "Permanecer Conectado"
        long expiration = rememberMe ? refreshLongExpiration : refreshShortExpiration;

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());

        return buildToken(usuario.getId(), usuario.getEmail(), expiration, claims);
    }

    // Implementar lógica de extração e validação (essencial para o filtro JWT)
    // public Claims extractClaims(String token) { ... }
}