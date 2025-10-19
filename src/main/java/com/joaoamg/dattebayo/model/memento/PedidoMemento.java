package com.joaoamg.dattebayo.model.memento;

import com.joaoamg.dattebayo.model.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedido_historico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoMemento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private BigDecimal valorTotal;
    private LocalDateTime dataEstado;
}
