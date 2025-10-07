package com.joaoamg.dattebayo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a segurança web
@EnableMethodSecurity(prePostEnabled = true) // Habilita as anotações @PreAuthorize nos Controllers
public class SecurityConfig {

    // 1. Define o PasswordEncoder (Você já deve ter esta parte)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Configura a cadeia de filtros de segurança (Security Filter Chain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desativa a proteção CSRF (necessário para APIs stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configura a política de sessão como STATELESS (sem cookies de sessão, dependente de JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define as regras de autorização para as requisições HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso irrestrito ao endpoint de registro de cliente e ao playground GraphQL
                        .requestMatchers("/api/auth/registrar", "/graphql", "/graphiql").permitAll()
                        // Todas as outras requisições devem ser autenticadas
                        .anyRequest().authenticated()
                );

        // NOTA: A lógica para ler e validar o JWT (JwtAuthenticationFilter) deve ser
        // injetada aqui (ex: http.addFilterBefore(...)).

        return http.build();
    }
}