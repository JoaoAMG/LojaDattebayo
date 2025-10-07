package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.model.NivelAcesso;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioAdministradorServiceTest {

    @InjectMocks
    private UsuarioAdministradorService administradorService;

    @Mock
    private UsuarioAdministradorRepository administradorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UUID adminId;
    private UsuarioAdministrador admin;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        admin = new UsuarioAdministrador("Admin Teste", "admin@dattebayo.com", "senhaadmin", NivelAcesso.MODERADOR);
        admin.setId(adminId);
    }

    // --- TESTES DE CRIAÇÃO (REGISTRO) ---

    @Test
    void registrarAdministrador_ComSucesso_DeveCodificarSenhaESalvar() {
        when(administradorRepository.findByEmail(admin.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(admin.getSenha())).thenReturn("hash_admin");
        when(administradorRepository.save(any(UsuarioAdministrador.class))).thenReturn(admin);

        UsuarioAdministrador salvo = administradorService.registrarAdministrador(admin);

        assertEquals("hash_admin", salvo.getSenha());
        assertEquals(NivelAcesso.MODERADOR, salvo.getNivelAcesso());
        verify(administradorRepository, times(1)).save(admin);
    }

    @Test
    void registrarAdministrador_ComEmailDuplicado_DeveLancarBusinessRuleException() {
        when(administradorRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThrows(BusinessRuleException.class, () -> {
            administradorService.registrarAdministrador(admin);
        });

        verify(administradorRepository, never()).save(any(UsuarioAdministrador.class));
    }

    // --- TESTES DE ATUALIZAÇÃO E DELETE ---

    @Test
    void atualizar_AdminNaoExiste_DeveLancarResourceNotFoundException() {
        when(administradorRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            administradorService.atualizar(admin);
        });
        verify(administradorRepository, never()).save(any(UsuarioAdministrador.class));
    }

    @Test
    void deletar_AdminNaoExiste_DeveLancarResourceNotFoundException() {
        when(administradorRepository.existsById(adminId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            administradorService.deletar(adminId);
        });
        verify(administradorRepository, never()).deleteById(any(UUID.class));
    }
}