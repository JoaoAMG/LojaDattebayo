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
        cliente = new UsuarioCliente("João Teste", "teste@email.com", "senha123", "12345678900", null);
        cliente.setId(clienteId);
    }

    // --- TESTES DE CRIAÇÃO (REGISTRO) ---

    @Test
    void registrarCliente_ComSucesso_DeveCodificarSenhaESalvar() {
        // Mock: Simula que o e-mail é único
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        // Mock: Simula o encoding da senha
        when(passwordEncoder.encode(cliente.getSenha())).thenReturn("hash_secreto");
        // Mock: Retorna o cliente salvo com o ID
        when(clienteRepository.save(any(UsuarioCliente.class))).thenReturn(cliente);

        UsuarioCliente salvo = clienteService.registrarCliente(cliente);

        // Verifica se a senha foi codificada antes de salvar
        assertEquals("hash_secreto", salvo.getSenha());
        assertNotNull(salvo.getDataCriacao());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void registrarCliente_ComEmailDuplicado_DeveLancarBusinessRuleException() {
        // Mock: Simula que o e-mail já existe
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        // Verifica se a exceção correta foi lançada
        assertThrows(BusinessRuleException.class, () -> {
            clienteService.registrarCliente(cliente);
        });

        // Verifica que o método save NUNCA foi chamado
        verify(clienteRepository, never()).save(any(UsuarioCliente.class));
    }

    // --- TESTES DE LEITURA (READ) ---

    @Test
    void buscarPorEmail_Encontrado_DeveRetornarCliente() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(cliente));

        UsuarioCliente encontrado = clienteService.buscarPorEmail(cliente.getEmail());

        assertEquals(clienteId, encontrado.getId());
    }

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

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(passwordEncoder.encode("nova_senha")).thenReturn("novo_hash");
        when(clienteRepository.save(any(UsuarioCliente.class))).thenReturn(clienteAtualizado);

        UsuarioCliente salvo = clienteService.atualizar(clienteAtualizado);

        assertEquals("novo_hash", salvo.getSenha());
        verify(passwordEncoder, times(1)).encode("nova_senha");
    }

    @Test
    void atualizar_ClienteNaoExiste_DeveLancarResourceNotFoundException() {
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.atualizar(cliente);
        });
        verify(clienteRepository, never()).save(any(UsuarioCliente.class));
    }
}