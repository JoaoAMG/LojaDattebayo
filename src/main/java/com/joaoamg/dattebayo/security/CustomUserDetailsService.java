package com.joaoamg.dattebayo.security;

import com.joaoamg.dattebayo.model.Usuario;
import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import com.joaoamg.dattebayo.repository.UsuarioClienteRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioClienteRepository clienteRepository;
    private final UsuarioAdministradorRepository administradorRepository;

    public CustomUserDetailsService(UsuarioClienteRepository clienteRepository, UsuarioAdministradorRepository administradorRepository) {
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<? extends Usuario> usuario = clienteRepository.findByEmail(email);

        if (usuario.isEmpty()) {
            usuario = administradorRepository.findByEmail(email);
        }

        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email);
        }

        return buildUserDetails(usuario.get());
    }


    private UserDetails buildUserDetails(Usuario usuario) {
        Collection<? extends GrantedAuthority> authorities;


        if (usuario instanceof UsuarioAdministrador administrador) {
            String role = administrador.getNivelAcesso().name();
            authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        } else {

            authorities = Collections.singletonList(new SimpleGrantedAuthority("CLIENTE"));
        }


        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                authorities
        );
    }
}