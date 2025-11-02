package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.dto.LoginRequest;
import com.joaoamg.dattebayo.dto.TokenResponse;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.model.Usuario;
import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import com.joaoamg.dattebayo.repository.UsuarioClienteRepository;
import com.joaoamg.dattebayo.service.JwtTokenService;
import com.joaoamg.dattebayo.service.UsuarioClienteService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UsuarioClienteService clienteService;
    private final UsuarioClienteRepository clienteRepository;
    private final UsuarioAdministradorRepository administradorRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UsuarioClienteService clienteService,
            UsuarioClienteRepository clienteRepository,
            UsuarioAdministradorRepository administradorRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
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

        Usuario usuario = clienteRepository.findByEmail(userDetails.getUsername())
                .<Usuario>map(c -> c)
                .orElseGet(() -> administradorRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new BusinessRuleException("Erro inesperado: Utilizador autenticado não encontrado.")));

        usuario.setUltimoLogin(LocalDateTime.now());
        if (usuario instanceof UsuarioCliente) {
            clienteRepository.save((UsuarioCliente) usuario);
        } else {
            administradorRepository.save((UsuarioAdministrador) usuario);
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
        throw new UnsupportedOperationException("A Mutation refreshToken precisa ser implementada.");
    }
}
