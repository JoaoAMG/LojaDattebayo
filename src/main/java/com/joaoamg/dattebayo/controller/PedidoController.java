package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.model.Pedido;
import com.joaoamg.dattebayo.model.ItemPedido;
import com.joaoamg.dattebayo.service.PedidoService;
import com.joaoamg.dattebayo.service.ItemPedidoService;
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
    private final ItemPedidoService itemPedidoService;

    public PedidoController(PedidoService pedidoService, ItemPedidoService itemPedidoService) {
        this.pedidoService = pedidoService;
        this.itemPedidoService = itemPedidoService;
    }




    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#id)")
    public Pedido pedido(@Argument UUID id) {

        return pedidoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "ID", id));
    }


    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#pedidoId)")
    public List<ItemPedido> itensPedido(@Argument UUID pedidoId) {
        return itemPedidoService.buscarItensPorPedido(pedidoId);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Pedido iniciarPedido(@Argument("pedidoInput") Pedido pedidoInput) {

        return pedidoService.criarPedidoInicial(pedidoInput);
    }


    @MutationMapping
    @PreAuthorize("@pedidoPermissionEvaluator.isOwner(#pedidoId)")
    public ItemPedido adicionarItemPedido(@Argument UUID pedidoId, @Argument("itemInput") ItemPedido itemInput) {

        return itemPedidoService.adicionarItem(itemInput);
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
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @pedidoPermissionEvaluator.isOwner(#pedidoInput.id)")
    public Pedido atualizarPedido(@Argument("pedidoInput") Pedido pedidoInput) {
        return pedidoService.atualizar(pedidoInput);
    }


    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR')")
    public UUID deletarPedido(@Argument UUID id) {
        pedidoService.deletar(id);
        return id;
    }


    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or @itemPedidoPermissionEvaluator.isOwner(#id)")
    public UUID removerItemPedido(@Argument UUID id) {
        // O service/evaluator precisa checar se o pedido pai do item pertence ao usuário
        itemPedidoService.removerItem(id);
        return id;
    }
}