package com.joaoamg.dattebayo.model.memento;


import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
public class PedidoMemento {


    private final PedidoStatus status;
    private final BigDecimal valorTotal;
    private final LocalDateTime dataPedido;

    public PedidoMemento(PedidoStatus status, BigDecimal valorTotal, LocalDateTime dataPedido) {
        this.status = status;
        this.valorTotal = valorTotal;
        this.dataPedido = dataPedido;
    }
}