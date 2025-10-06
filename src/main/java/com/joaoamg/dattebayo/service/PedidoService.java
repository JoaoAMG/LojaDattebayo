package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Pedido;
import com.joaoamg.dattebayo.model.memento.PedidoStatus;
import com.joaoamg.dattebayo.model.memento.HistoricoPedido;
import com.joaoamg.dattebayo.repository.PedidoRepository;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final Map<UUID, HistoricoPedido> historicos = new HashMap<>();

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }


    public Pedido criarPedidoInicial(Pedido pedido) {
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(PedidoStatus.NAO_EFETUADO);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);


        HistoricoPedido historico = new HistoricoPedido(pedidoSalvo);
        historicos.put(pedidoSalvo.getId(), historico);

        return pedidoSalvo;
    }


    public Optional<Pedido> buscarPorId(UUID id) {
        return pedidoRepository.findById(id);
    }


    public Pedido atualizar(Pedido pedido) {
        if (pedido.getId() == null || !pedidoRepository.existsById(pedido.getId())) {

            throw new ResourceNotFoundException("Pedido", "ID", pedido.getId());
        }

        return pedidoRepository.save(pedido);
    }


    public void deletar(UUID id) {
        if (!pedidoRepository.existsById(id)) {

            throw new ResourceNotFoundException("Pedido", "ID", id);
        }
        historicos.remove(id);
        pedidoRepository.deleteById(id);
    }


    public Pedido confirmarPedido(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "ID", pedidoId));

        HistoricoPedido historico = historicos.get(pedidoId);
        if (historico == null) {
            historico = new HistoricoPedido(pedido);
            historicos.put(pedidoId, historico);
        }

        pedido.setStatus(PedidoStatus.EFETUADO);
        historico.salvarEstado();

        return pedidoRepository.save(pedido);
    }

    public Pedido desfazerConfirmacao(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "ID", pedidoId));

        HistoricoPedido historico = historicos.get(pedidoId);

        if (historico != null && historico.desfazerOperacao()) {
            return pedidoRepository.save(pedido);
        } else {

            throw new BusinessRuleException("Não é possível desfazer a operação para este pedido. Histórico incompleto ou estado inicial.");
        }
    }
}