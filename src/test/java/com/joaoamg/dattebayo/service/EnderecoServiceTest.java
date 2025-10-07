package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Endereco;
import com.joaoamg.dattebayo.repository.EnderecoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnderecoServiceTest {

    @InjectMocks
    private EnderecoService enderecoService;

    @Mock
    private EnderecoRepository enderecoRepository;

    private UUID enderecoId;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        enderecoId = UUID.randomUUID();
        endereco = Endereco.builder()
                .id(enderecoId)
                .cep("12345-678")
                .logradouro("Rua Teste")
                .build();
    }

    // --- TESTES DE CRIAÇÃO E LEITURA ---

    @Test
    void criar_DeveSalvarEndereco() {
        when(enderecoRepository.save(endereco)).thenReturn(endereco);
        Endereco salvo = enderecoService.criar(endereco);
        assertNotNull(salvo.getId());
        verify(enderecoRepository, times(1)).save(endereco);
    }

    @Test
    void buscarPorId_NaoEncontrado_RetornaOptionalVazio() {
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.empty());
        Optional<Endereco> resultado = enderecoService.buscarPorId(enderecoId);
        assertTrue(resultado.isEmpty());
    }

    // --- TESTES DE ATUALIZAÇÃO E DELETE ---

    @Test
    void atualizar_EnderecoNaoExiste_DeveLancarResourceNotFoundException() {
        when(enderecoRepository.existsById(enderecoId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.atualizar(endereco);
        });
        verify(enderecoRepository, never()).save(any(Endereco.class));
    }

    @Test
    void deletar_EnderecoNaoExiste_DeveLancarResourceNotFoundException() {
        when(enderecoRepository.existsById(enderecoId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.deletar(enderecoId);
        });
        verify(enderecoRepository, never()).deleteById(any(UUID.class));
    }
}