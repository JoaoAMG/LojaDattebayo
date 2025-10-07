package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Pedido;
import com.joaoamg.dattebayo.model.memento.PedidoStatus;
import com.joaoamg.dattebayo.repository.PedidoRepository;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    // Captor para verificar o estado do pedido antes de salvar
    @Captor
    private ArgumentCaptor<Pedido> pedidoCaptor;

    private UUID pedidoId;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID();
        pedido = Pedido.builder().id(pedidoId).status(PedidoStatus.NAO_EFETUADO).build();
    }

    // --- TESTES DO FLUXO MEMENTO ---

    @Test
    void confirmarPedido_PedidoExiste_DeveMudarStatusESalvar() {
        // Mock: Simula que o pedido foi encontrado
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        // Mock: Retorna o pedido após o save
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido confirmado = pedidoService.confirmarPedido(pedidoId);

        // Verifica o estado final
        assertEquals(PedidoStatus.EFETUADO, confirmado.getStatus());

        // Verifica se o save foi chamado
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void desfazerConfirmacao_AposConfirmado_DeveRetornarParaNaoEfetuado() {
        // 1. Simula o estado EFETUADO inicial (após a confirmação)
        pedido.setStatus(PedidoStatus.EFETUADO);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // 2. Chama o método de criação inicial para popular o Memento (CareTaker)
        // Isso garante que o Memento tenha o estado NAO_EFETUADO (inicial) e EFETUADO (atual)
        pedidoService.criarPedidoInicial(pedido); // Salva NAO_EFETUADO
        pedidoService.confirmarPedido(pedidoId);   // Salva EFETUADO

        // 3. Chama o desfazer
        Pedido desfeito = pedidoService.desfazerConfirmacao(pedidoId);

        // Verifica se o status voltou para o estado anterior
        assertEquals(PedidoStatus.NAO_EFETUADO, desfeito.getStatus());

        // Verifica que o save foi chamado
        verify(pedidoRepository, times(3)).save(any(Pedido.class)); // 1.criar 2.confirmar 3.desfazer
    }

    @Test
    void desfazerConfirmacao_SemHistorico_DeveLancarBusinessRuleException() {
        // Mock: Simula o pedido encontrado
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        // Não chamamos criarPedidoInicial, então o histórico está vazio/incompleto

        assertThrows(BusinessRuleException.class, () -> {
            pedidoService.desfazerConfirmacao(pedidoId);
        });

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    // --- TESTES DE CRIAÇÃO ---

    @Test
    void criarPedidoInicial_DeveComecarComoNaoEfetuado() {
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido criado = pedidoService.criarPedidoInicial(pedido);

        assertEquals(PedidoStatus.NAO_EFETUADO, criado.getStatus());
        assertNotNull(criado.getDataPedido());
    }

    // --- TESTES DE DELETE ---

    @Test
    void deletar_PedidoExiste_DeveDeletarERemoverHistorico() {
        when(pedidoRepository.existsById(pedidoId)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(pedidoId);

        pedidoService.deletar(pedidoId);

        verify(pedidoRepository, times(1)).deleteById(pedidoId);
    }

    @Test
    void deletar_PedidoNaoExiste_DeveLancarResourceNotFoundException() {
        when(pedidoRepository.existsById(pedidoId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.deletar(pedidoId);
        });

        verify(pedidoRepository, never()).deleteById(any(UUID.class));
    }
}