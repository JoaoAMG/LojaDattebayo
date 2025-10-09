package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.repository.UsuarioClienteRepository;
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
public class UsuarioClienteServiceTest {

    @InjectMocks
    private UsuarioClienteService clienteService;

    @Mock
    private UsuarioClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UUID clienteId;
    private UsuarioCliente cliente;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        // O Endereco é null por simplicidade nos testes unitários
        cliente = new UsuarioCliente("João Teste", "teste@email.com", "senha123", "12345678900", null);
        cliente.setId(clienteId);
    }

    // --- TESTES DE CRIAÇÃO (REGISTRO) ---

    @Test
    void registrarCliente_ComSucesso_DeveCodificarSenhaESalvar() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(cliente.getSenha())).thenReturn("hash_secreto");
        when(clienteRepository.save(any(UsuarioCliente.class))).thenReturn(cliente);

        UsuarioCliente salvo = clienteService.registrarCliente(cliente);

        assertEquals("hash_secreto", salvo.getSenha());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void registrarCliente_ComEmailDuplicado_DeveLancarBusinessRuleException() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        assertThrows(BusinessRuleException.class, () -> {
            clienteService.registrarCliente(cliente);
        });

        verify(clienteRepository, never()).save(any(UsuarioCliente.class));
    }

    // --- TESTES DE LEITURA (READ) ---

    @Test
    void buscarPorEmail_NaoEncontrado_DeveLancarResourceNotFoundException() {
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.buscarPorEmail("naoexiste@email.com");
        });
    }

    // --- TESTES DE ATUALIZAÇÃO (UPDATE) ---

    @Test
    void atualizar_ComNovaSenha_DeveCodificarESalvar() {
        UsuarioCliente clienteAtualizado = new UsuarioCliente(
                "João Novo Nome", cliente.getEmail(), "nova_senha", cliente.getCpf(), cliente.getEndereco()
        );
        clienteAtualizado.setId(clienteId);

        // ✅ CORREÇÃO: Simula que o cliente antigo foi encontrado (para a lógica de atualização no service)
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.encode("nova_senha")).thenReturn("novo_hash");
        when(clienteRepository.save(any(UsuarioCliente.class))).thenReturn(clienteAtualizado);

        UsuarioCliente salvo = clienteService.atualizar(clienteAtualizado);

        assertEquals("novo_hash", salvo.getSenha());
        verify(passwordEncoder, times(1)).encode("nova_senha");
    }

    @Test
    void atualizar_ClienteNaoExiste_DeveLancarResourceNotFoundException() {
        // ✅ CORREÇÃO: Simula que o findById retorna vazio
        when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.atualizar(cliente);
        });

        // Verifica que o save NUNCA foi chamado
        verify(clienteRepository, never()).save(any(UsuarioCliente.class));
    }

    // --- TESTES DE DELETE ---

    @Test
    void deletar_ClienteExiste_DeveDeletarComSucesso() {
        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(clienteId);

        // Não deve lançar exceção
        assertDoesNotThrow(() -> clienteService.deletar(clienteId));

        verify(clienteRepository, times(1)).deleteById(clienteId);
    }

    @Test
    void deletar_ClienteNaoExiste_DeveLancarResourceNotFoundException() {
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.deletar(clienteId);
        });

        verify(clienteRepository, never()).deleteById(any(UUID.class));
    }
}