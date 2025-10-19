package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.ItemPedido;
import com.joaoamg.dattebayo.repository.ItemPedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<ItemPedido> buscarItensPorPedido(UUID pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId);
    }
}
