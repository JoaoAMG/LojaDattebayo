package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.dto.LoginRequest;
import com.joaoamg.dattebayo.dto.TokenResponse;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.model.Usuario;
import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.service.JwtTokenService;
import com.joaoamg.dattebayo.service.UsuarioClienteService;
import com.joaoamg.dattebayo.service.UsuarioService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UsuarioClienteService clienteService;
    private final UsuarioService usuarioService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UsuarioClienteService clienteService,
            UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @MutationMapping
    @PreAuthorize("isAnonymous()")
    public UsuarioCliente registrarCliente(@Argument("clienteInput") UsuarioCliente clienteInput) {
        return clienteService.registrarCliente(clienteInput);
    }

    @MutationMapping
    @PreAuthorize("isAnonymous()")
    public TokenResponse login(@Argument("loginInput") LoginRequest loginInput) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginInput.getEmail(), loginInput.getSenha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        Usuario usuario = usuarioService.findUsuarioByEmail(userDetails.getUsername());


        if (usuario == null) {
            throw new BusinessRuleException("Erro inesperado: Usuário autenticado mas não encontrado no banco de dados.");
        }

        String accessToken = jwtTokenService.generateAccessToken(usuario);
        String refreshToken = jwtTokenService.generateRefreshToken(usuario, loginInput.isRememberMe());

        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @MutationMapping
    @PreAuthorize("permitAll")
    public TokenResponse refreshToken(@Argument String refreshToken) {
        if (!jwtTokenService.isRefreshTokenValid(refreshToken)) {
            throw new BusinessRuleException("Refresh token inválido ou expirado.");
        }

        String userEmail = jwtTokenService.extractUsername(refreshToken);
        Usuario usuario = usuarioService.findUsuarioByEmail(userEmail);
        if (usuario == null) {
            throw new BusinessRuleException("Usuário associado ao refresh token não encontrado.");
        }

        String newAccessToken = jwtTokenService.generateAccessToken(usuario);

        TokenResponse response = new TokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }
}