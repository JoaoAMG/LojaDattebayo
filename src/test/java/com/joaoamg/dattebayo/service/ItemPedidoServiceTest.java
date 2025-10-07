package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.ItemPedido;
import com.joaoamg.dattebayo.repository.ItemPedidoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemPedidoServiceTest {

    @InjectMocks
    private ItemPedidoService itemPedidoService;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Captor
    private ArgumentCaptor<ItemPedido> itemPedidoCaptor;

    private UUID itemId;
    private ItemPedido item;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        item = ItemPedido.builder()
                .id(itemId)
                .precoUnitario(new BigDecimal("10.00"))
                .quantidade(5)
                .build();
    }

    // --- TESTES DE CRIAÇÃO ---

    @Test
    void adicionarItem_DeveCalcularSubTotalCorretamenteESalvar() {
        when(itemPedidoRepository.save(any(ItemPedido.class))).thenReturn(item);

        itemPedidoService.adicionarItem(item);

        // Verifica se o método save foi chamado e captura o objeto
        verify(itemPedidoRepository).save(itemPedidoCaptor.capture());
        ItemPedido itemCapturado = itemPedidoCaptor.getValue();

        // Verifica a regra de negócio: 10.00 * 5 = 50.00
        assertEquals(new BigDecimal("50.00"), itemCapturado.getSubTotal());
    }

    // --- TESTES DE ATUALIZAÇÃO ---

    @Test
    void atualizarItem_DeveRecalcularSubTotalComNovosValores() {
        ItemPedido itemAtualizado = ItemPedido.builder()
                .id(itemId)
                .precoUnitario(new BigDecimal("20.00"))
                .quantidade(3) // Quantidade mudou
                .build();

        when(itemPedidoRepository.existsById(itemId)).thenReturn(true);
        when(itemPedidoRepository.save(any(ItemPedido.class))).thenReturn(itemAtualizado);

        itemPedidoService.atualizar(itemAtualizado);

        verify(itemPedidoRepository).save(itemPedidoCaptor.capture());
        ItemPedido itemCapturado = itemPedidoCaptor.getValue();

        // Verifica a regra de negócio: 20.00 * 3 = 60.00
        assertEquals(new BigDecimal("60.00"), itemCapturado.getSubTotal());
    }

    @Test
    void atualizarItem_ItemNaoExiste_DeveLancarResourceNotFoundException() {
        when(itemPedidoRepository.existsById(itemId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            itemPedidoService.atualizar(item);
        });
        verify(itemPedidoRepository, never()).save(any(ItemPedido.class));
    }
}