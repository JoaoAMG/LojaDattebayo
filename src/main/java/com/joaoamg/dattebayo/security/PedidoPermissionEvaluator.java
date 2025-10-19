package com.joaoamg.dattebayo.security;

import com.joaoamg.dattebayo.model.ItemPedido;
import com.joaoamg.dattebayo.repository.ItemPedidoRepository;
import com.joaoamg.dattebayo.repository.PedidoRepository;
import com.joaoamg.dattebayo.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component("pedidoPermissionEvaluator")
public class PedidoPermissionEvaluator {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioService usuarioService;

    public PedidoPermissionEvaluator(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository, UsuarioService usuarioService) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.usuarioService = usuarioService;
    }

    public boolean isOwner(Authentication authentication, UUID pedidoId) {
        if (authentication == null) return false;

        final UUID authenticatedUserId = usuarioService.getAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) return false;

        return pedidoRepository.findById(pedidoId)
                .map(pedido -> pedido.getUsuario().getId().equals(authenticatedUserId))
                .orElse(false);
    }

    public boolean isOwnerByItemId(Authentication authentication, UUID itemPedidoId) {
        if (authentication == null) return false;

        final UUID authenticatedUserId = usuarioService.getAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) return false;

        return itemPedidoRepository.findById(itemPedidoId)
                .map(ItemPedido::getPedido)
                .map(pedido -> pedido.getUsuario().getId().equals(authenticatedUserId))
                .orElse(false);
    }

    public boolean isOwnerOfUser(Authentication authentication, UUID usuarioId) {
        if (authentication == null) return false;

        final UUID authenticatedUserId = usuarioService.getAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) return false;

        return authenticatedUserId.equals(usuarioId);
    }
}
