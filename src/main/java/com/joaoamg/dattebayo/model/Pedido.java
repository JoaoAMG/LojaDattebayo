package com.joaoamg.dattebayo.model;
import com.joaoamg.dattebayo.model.memento.PedidoMemento; // Importar o Memento
import com.joaoamg.dattebayo.model.memento.PedidoStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioCliente usuario;

    @Enumerated(EnumType.STRING)
    private MeioPagamento meioPagamento;

    private BigDecimal valorTotal;
    private LocalDateTime dataPedido;


    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;




    public PedidoMemento salvarEstado() {
        return new PedidoMemento(this.status, this.valorTotal, this.dataPedido);
    }


    public void restaurarEstado(PedidoMemento memento) {
        this.status = memento.getStatus();
        this.valorTotal = memento.getValorTotal();
        this.dataPedido = memento.getDataPedido();

    }
}