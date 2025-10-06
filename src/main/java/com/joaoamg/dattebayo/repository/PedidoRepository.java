package com.joaoamg.dattebayo.repository;

import com.joaoamg.dattebayo.model.Pedido;
import com.joaoamg.dattebayo.model.memento.PedidoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

     List<Pedido> findByUsuarioId(UUID usuarioId);
     List<Pedido> findByStatus(PedidoStatus status);
}