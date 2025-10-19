package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Usuario;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import com.joaoamg.dattebayo.repository.UsuarioClienteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UsuarioService {

    private final UsuarioClienteRepository clienteRepository;
    private final UsuarioAdministradorRepository administradorRepository;

    public UsuarioService(UsuarioClienteRepository clienteRepository, UsuarioAdministradorRepository administradorRepository) {
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
    }

    public Usuario findUsuarioByEmail(String email) {
        return clienteRepository.findByEmail(email)
                .<Usuario>map(c -> c)
                .orElseGet(() -> administradorRepository.findByEmail(email)
                        .orElse(null));
    }


    public UUID getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName();
        Usuario usuario = findUsuarioByEmail(email);
        return (usuario != null) ? usuario.getId() : null;
    }
}