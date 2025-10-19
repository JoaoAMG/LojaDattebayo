package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Usuario;
import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.model.UsuarioCliente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.short-expiration}")
    private long refreshShortExpiration;

    @Value("${jwt.refresh.long-expiration}")
    private long refreshLongExpiration;



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    public String generateAccessToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId().toString());
        claims.put("nome", usuario.getNome());

        if (usuario instanceof UsuarioAdministrador administrador) {
            claims.put("tipo", "ADMIN");
            claims.put("nivelAcesso", administrador.getNivelAcesso().name());
        } else if (usuario instanceof UsuarioCliente) {
            claims.put("tipo", "CLIENTE");
        }

        return buildToken(claims, usuario.getEmail(), accessExpiration);
    }

    public String generateRefreshToken(Usuario usuario, boolean rememberMe) {
        long expirationTime = rememberMe ? refreshLongExpiration : refreshShortExpiration;

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        return buildToken(claims, usuario.getEmail(), expirationTime);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    public boolean isRefreshTokenValid(String token) {
        final Claims claims = extractAllClaims(token);
        String tokenType = claims.get("type", String.class);
        return !isTokenExpired(token) && "REFRESH".equals(tokenType);
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }



    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
