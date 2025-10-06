package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioAdministradorService {

    private final UsuarioAdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdministradorService(UsuarioAdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UsuarioAdministrador registrarAdministrador(UsuarioAdministrador admin) {
        if (administradorRepository.findByEmail(admin.getEmail()).isPresent()) {

            throw new BusinessRuleException("O e-mail '" + admin.getEmail() + "' já está cadastrado para um administrador.");
        }
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        admin.setDataCriacao(LocalDateTime.now());
        admin.setAtivo(true);
        return administradorRepository.save(admin);
    }


    public UsuarioAdministrador atualizar(UsuarioAdministrador admin) {
        if (admin.getId() == null) {
            throw new BusinessRuleException("ID do Administrador é obrigatório para atualização.");
        }

        UsuarioAdministrador adminExistente = administradorRepository.findById(admin.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", "ID", admin.getId()));


        if (admin.getSenha() != null && !admin.getSenha().isEmpty()) {
            admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        } else {

            admin.setSenha(adminExistente.getSenha());
        }

        admin.setDataAtualizacao(LocalDateTime.now());
        return administradorRepository.save(admin);
    }


    public void deletar(UUID id) {
        if (!administradorRepository.existsById(id)) {

            throw new ResourceNotFoundException("Administrador", "ID", id);
        }
        administradorRepository.deleteById(id);
    }
}