package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.ItemPedido;
import com.joaoamg.dattebayo.repository.ItemPedidoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }


    private void recalcularSubTotal(ItemPedido item) {
        if (item.getPrecoUnitario() != null && item.getQuantidade() != null) {
            item.setSubTotal(item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())));
        } else {
            item.setSubTotal(BigDecimal.ZERO);
        }
    }


    public ItemPedido adicionarItem(ItemPedido item) {
        recalcularSubTotal(item);
        return itemPedidoRepository.save(item);
    }


    public List<ItemPedido> buscarItensPorPedido(UUID pedidoId) {

        return itemPedidoRepository.findAll();
    }


    public ItemPedido atualizar(ItemPedido item) {
        if (item.getId() == null || !itemPedidoRepository.existsById(item.getId())) {

            throw new ResourceNotFoundException("Item de Pedido", "ID", item.getId());
        }

        recalcularSubTotal(item);

        return itemPedidoRepository.save(item);
    }


    public void removerItem(UUID id) {
        if (!itemPedidoRepository.existsById(id)) {

            throw new ResourceNotFoundException("Item de Pedido", "ID", id);
        }
        itemPedidoRepository.deleteById(id);
    }
}