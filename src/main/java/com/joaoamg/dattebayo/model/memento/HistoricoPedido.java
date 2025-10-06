package com.joaoamg.dattebayo.model.memento;

import com.joaoamg.dattebayo.model.Pedido;

import java.util.ArrayDeque;
import java.util.Deque;


public class HistoricoPedido {


    private final Deque<PedidoMemento> historico = new ArrayDeque<>();
    private final Pedido pedido;

    public HistoricoPedido(Pedido pedido) {
        this.pedido = pedido;

        salvarEstado();
    }


    public void salvarEstado() {
        historico.push(pedido.salvarEstado());
        System.out.println("Estado do Pedido salvo. Status: " + pedido.getStatus());
    }


    public boolean desfazerOperacao() {

        if (historico.size() > 1) {

            historico.pop();

            PedidoMemento mementoAnterior = historico.peek();

            if (mementoAnterior != null) {

                pedido.restaurarEstado(mementoAnterior);
                System.out.println("Operação desfeita. Novo Status: " + pedido.getStatus());
                return true;
            }
        }
        System.out.println("Não há estados anteriores para desfazer.");
        return false;
    }
}