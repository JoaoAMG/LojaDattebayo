package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.dto.ItemPedidoInputDTO;
import com.joaoamg.dattebayo.dto.PedidoUpdateInputDTO;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import com.joaoamg.dattebayo.model.*;
import com.joaoamg.dattebayo.model.memento.HistoricoPedido;
import com.joaoamg.dattebayo.model.memento.PedidoStatus;
import com.joaoamg.dattebayo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioClienteRepository usuarioClienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final Map<UUID, HistoricoPedido> historicos = new HashMap<>();

    public PedidoService(PedidoRepository pedidoRepository, UsuarioClienteRepository usuarioClienteRepository, ProdutoRepository produtoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioClienteRepository = usuarioClienteRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional
    public Pedido iniciarPedido(UUID usuarioId) {
        UsuarioCliente usuario = usuarioClienteRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário Cliente", "ID", usuarioId));

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .dataPedido(LocalDateTime.now())
                .status(PedidoStatus.NAO_EFETUADO)
                .itens(new ArrayList<>())
                .valorTotal(BigDecimal.ZERO)
                .build();

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        HistoricoPedido historico = new HistoricoPedido(pedidoSalvo);
        historicos.put(pedidoSalvo.getId(), historico);

        return pedidoSalvo;
    }

    @Transactional
    public Pedido adicionarItem(UUID pedidoId, ItemPedidoInputDTO itemInput) {
        Pedido pedido = findPedidoById(pedidoId);
        Produto produto = produtoRepository.findById(itemInput.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "ID", itemInput.getProdutoId()));

        if (pedido.getStatus() == PedidoStatus.EFETUADO) {
            throw new BusinessRuleException("Não é possível adicionar itens a um pedido já efetuado.");
        }

        Optional<ItemPedido> itemExistente = pedido.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemPedido item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + itemInput.getQuantidade());
            item.recalcularSubTotal();
        } else {
            ItemPedido novoItem = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemInput.getQuantidade())
                    .precoUnitario(produto.getValor())
                    .build();
            novoItem.recalcularSubTotal();
            pedido.getItens().add(novoItem);
        }

        recalcularValorTotal(pedido);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido removerItem(UUID itemPedidoId) {
        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Pedido", "ID", itemPedidoId));

        Pedido pedido = item.getPedido();
        if (pedido.getStatus() == PedidoStatus.EFETUADO) {
            throw new BusinessRuleException("Não é possível remover itens de um pedido já efetuado.");
        }

        pedido.getItens().remove(item);
        itemPedidoRepository.delete(item);
        recalcularValorTotal(pedido);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarQuantidadeItem(UUID itemPedidoId, int quantidade) {
        if (quantidade <= 0) {
            return removerItem(itemPedidoId);
        }

        ItemPedido item = itemPedidoRepository.findById(itemPedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Pedido", "ID", itemPedidoId));

        Pedido pedido = item.getPedido();
        if (pedido.getStatus() == PedidoStatus.EFETUADO) {
            throw new BusinessRuleException("Não é possível atualizar itens de um pedido já efetuado.");
        }

        item.setQuantidade(quantidade);
        item.recalcularSubTotal();
        recalcularValorTotal(pedido);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizar(UUID id, PedidoUpdateInputDTO pedidoInput) {
        Pedido pedido = findPedidoById(id);

        if (pedidoInput.getMeioPagamento() != null) {
            pedido.setMeioPagamento(pedidoInput.getMeioPagamento());
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void deletar(UUID id) {
        Pedido pedido = findPedidoById(id);
        if (pedido.getStatus() == PedidoStatus.EFETUADO) {
            throw new BusinessRuleException("Não é possível deletar um pedido já efetuado.");
        }
        historicos.remove(id);
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public Pedido confirmarPedido(UUID pedidoId) {
        Pedido pedido = findPedidoById(pedidoId);

        HistoricoPedido historico = historicos.computeIfAbsent(pedidoId, k -> new HistoricoPedido(pedido));

        if (pedido.getItens().isEmpty()) {
            throw new BusinessRuleException("Não é possível confirmar um pedido sem itens.");
        }

        pedido.setStatus(PedidoStatus.EFETUADO);
        historico.salvarEstado();

        return pedidoRepository.save(pedido);
    }

    /**
     * ✅ LÓGICA CORRIGIDA: Agora salva o objeto Pedido correto, que foi modificado pelo Memento.
     */
    @Transactional
    public Pedido desfazerConfirmacao(UUID pedidoId) {
        HistoricoPedido historico = historicos.get(pedidoId);

        if (historico != null && historico.desfazerOperacao()) {
            // Pega o objeto Pedido que foi efetivamente modificado pelo Memento
            Pedido pedidoModificado = historico.getPedido();
            // Salva esse objeto no banco de dados
            return pedidoRepository.save(pedidoModificado);
        } else {
            throw new BusinessRuleException("Não é possível desfazer a operação para este pedido.");
        }
    }

    public Optional<Pedido> buscarPorId(UUID id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> buscarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> buscarPorUsuario(UUID usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    private Pedido findPedidoById(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "ID", id));
    }

    private void recalcularValorTotal(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(ItemPedido::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(total);
    }
}