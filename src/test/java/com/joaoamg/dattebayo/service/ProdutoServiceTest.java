package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Produto;
import com.joaoamg.dattebayo.repository.ProdutoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    private UUID produtoId;
    private Produto produto;

    @BeforeEach
    void setUp() {
        produtoId = UUID.randomUUID();
        produto = Produto.builder()
                .id(produtoId)
                .nome("Naruto Vol. 1")
                .genero("Shonen")
                .valor(new BigDecimal("19.90"))
                .build();
    }

    // --- TESTES DE BUSCA ---

    @Test
    void buscarTodos_DeveRetornarListaDeProdutos() {
        List<Produto> produtos = Arrays.asList(produto, new Produto());
        when(produtoRepository.findAll()).thenReturn(produtos);

        List<Produto> resultado = produtoService.buscarTodos();

        assertEquals(2, resultado.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorGenero_DeveChamarMetodoCustomizado() {
        List<Produto> shonenList = List.of(produto);
        // Mock: Simula a resposta do método findByGenero
        when(produtoRepository.findByGenero("Shonen")).thenReturn(shonenList);

        List<Produto> resultado = produtoService.buscarPorGenero("Shonen");

        assertEquals(1, resultado.size());
        assertEquals("Shonen", resultado.get(0).getGenero());
        // Verifica se o método customizado foi chamado no Repositório
        verify(produtoRepository, times(1)).findByGenero("Shonen");
    }

    // --- TESTES DE ATUALIZAÇÃO ---

    @Test
    void atualizar_ComSucesso_DeveSalvarProduto() {
        Produto atualizacao = Produto.builder().id(produtoId).nome("Naruto Vol. 2").build();
        when(produtoRepository.existsById(produtoId)).thenReturn(true);
        when(produtoRepository.save(atualizacao)).thenReturn(atualizacao);

        Produto salvo = produtoService.atualizar(atualizacao);

        assertEquals("Naruto Vol. 2", salvo.getNome());
        verify(produtoRepository, times(1)).save(atualizacao);
    }

    @Test
    void atualizar_ProdutoNaoExiste_DeveLancarResourceNotFoundException() {
        Produto produtoInexistente = Produto.builder().id(UUID.randomUUID()).build();
        when(produtoRepository.existsById(any(UUID.class))).thenReturn(false);

        // Verifica o lançamento da exceção
        assertThrows(ResourceNotFoundException.class, () -> {
            produtoService.atualizar(produtoInexistente);
        });

        verify(produtoRepository, never()).save(any(Produto.class));
    }
}