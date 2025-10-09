package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.dto.LoginRequest;
import com.joaoamg.dattebayo.dto.TokenResponse;
import com.joaoamg.dattebayo.model.Usuario; // Classe base
import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.service.JwtTokenService;
import com.joaoamg.dattebayo.service.UsuarioClienteService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

// O AuthController deve ser levemente protegido (rate limiting, etc.)
// mas os endpoints de login e registro são publicamente acessíveis.
@Controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UsuarioClienteService clienteService;

    // Injeção de dependência via construtor
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UsuarioClienteService clienteService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.clienteService = clienteService;
    }

    // --- 1. REGISTRO (CREATE) ---

    /**
     * Permite a criação de uma nova conta de cliente.
     * Esta Mutation é o ponto de entrada de segurança para a criação da identidade.
     */
    @MutationMapping
    @PreAuthorize("isAnonymous()") // Garante que apenas usuários NÃO autenticados podem se registrar
    public UsuarioCliente registrarCliente(@Argument("clienteInput") UsuarioCliente clienteInput) {
        // O Service cuida do hashing da senha, validação de e-mail único e persistência.
        return clienteService.registrarCliente(clienteInput);
    }

    // --- 2. LOGIN (AUTENTICAÇÃO E GERAÇÃO DE TOKEN) ---

    /**
     * Processa a autenticação do usuário e emite o Access Token e o Refresh Token.
     * * @param loginInput Contém email, senha e a flag 'rememberMe'.
     * @return DTO com os tokens emitidos.
     */
    @MutationMapping
    @PreAuthorize("isAnonymous()") // Garante que a chamada só é feita por quem não está logado
    public TokenResponse login(@Argument("loginInput") LoginRequest loginInput) {

        // 1. Prova de Identidade (Autenticação)
        // O AuthenticationManager usa a senha em texto puro para comparar com o hash no DB.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginInput.getEmail(), loginInput.getSenha())
        );

        // 2. Recupera o objeto do usuário (Principal)
        // O objeto retornado é o Usuario (ou UsuarioCliente/Administrador) carregado do DB.
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // 3. Geração dos Tokens (Access e Refresh)
        String accessToken = jwtTokenService.generateAccessToken(usuario);
        String refreshToken = jwtTokenService.generateRefreshToken(usuario, loginInput.isRememberMe());

        // 4. Retorna a resposta
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    // --- 3. RENOVAÇÃO DE TOKEN (Opcional, mas Essencial) ---

    /**
     * Permite obter um novo Access Token usando um Refresh Token válido.
     * * NOTA: Este método exige que você finalize o JwtTokenService com a lógica de
     * validação de Refresh Token (extração de claims, verificação de expiração, etc.).
     */
    @MutationMapping
    @PreAuthorize("isAnonymous()") // O refresh token é validado antes da autenticação
    public TokenResponse refreshToken(@Argument String refreshToken) {
        // Exemplo: Validação e Renovação
        // Usuario usuario = jwtTokenService.validateRefreshToken(refreshToken);
        // String newAccessToken = jwtTokenService.generateAccessToken(usuario);

        // Retornar um novo TokenResponse com o novo Access Token.
        throw new UnsupportedOperationException("A Mutation refreshToken precisa ser implementada no JwtTokenService.");
    }
}