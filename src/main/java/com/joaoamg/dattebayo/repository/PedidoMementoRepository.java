package com.joaoamg.dattebayo.repository;

import com.joaoamg.dattebayo.model.memento.PedidoMemento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoMementoRepository extends JpaRepository<PedidoMemento, UUID> {


    List<PedidoMemento> findByPedidoIdOrderByDataEstadoDesc(UUID pedidoId);
}
