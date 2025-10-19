package com.joaoamg.dattebayo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subTotal;


    public void recalcularSubTotal() {
        if (this.precoUnitario != null && this.quantidade != null) {
            this.subTotal = this.precoUnitario.multiply(new BigDecimal(this.quantidade));
        } else {
            this.subTotal = BigDecimal.ZERO;
        }
    }
}
