package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.model.Pedido;
import com.joaoamg.dattebayo.dto.ItemPedidoInputDTO;
import com.joaoamg.dattebayo.dto.PedidoUpdateInputDTO;
import com.joaoamg.dattebayo.service.PedidoService;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }



    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#id)")
    public Pedido pedido(@Argument UUID id) {
        return pedidoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "ID", id));
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR')")
    public List<Pedido> pedidos() {
        return pedidoService.buscarTodos();
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwnerOfUser(#usuarioId)")
    public List<Pedido> pedidosPorUsuario(@Argument UUID usuarioId) {
        return pedidoService.buscarPorUsuario(usuarioId);
    }



    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Pedido iniciarPedido(@Argument UUID usuarioId) {
        return pedidoService.iniciarPedido(usuarioId);
    }

    @MutationMapping
    @PreAuthorize("@pedidoPermissionEvaluator.isOwner(#pedidoId)")
    public Pedido adicionarItemAoPedido(@Argument UUID pedidoId, @Argument("itemInput") ItemPedidoInputDTO itemInput) {
        return pedidoService.adicionarItem(pedidoId, itemInput);
    }

    @MutationMapping
    @PreAuthorize("@pedidoPermissionEvaluator.isOwnerByItemId(#itemPedidoId)")
    public Pedido removerItemDoPedido(@Argument UUID itemPedidoId) {
        return pedidoService.removerItem(itemPedidoId);
    }

    @MutationMapping
    @PreAuthorize("@pedidoPermissionEvaluator.isOwnerByItemId(#itemPedidoId)")
    public Pedido atualizarItemDoPedido(@Argument UUID itemPedidoId, @Argument int quantidade) {
        return pedidoService.atualizarQuantidadeItem(itemPedidoId, quantidade);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#id)")
    public Pedido atualizarPedido(@Argument UUID id, @Argument("pedidoInput") PedidoUpdateInputDTO pedidoInput) {
        return pedidoService.atualizar(id, pedidoInput);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#pedidoId)")
    public Pedido confirmarPedido(@Argument UUID pedidoId) {
        return pedidoService.confirmarPedido(pedidoId);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#pedidoId)")
    public Pedido desfazerConfirmacao(@Argument UUID pedidoId) {
        return pedidoService.desfazerConfirmacao(pedidoId);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public UUID deletarPedido(@Argument UUID id) {
        pedidoService.deletar(id);
        return id;
    }
}
