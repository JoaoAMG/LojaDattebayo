package com.joaoamg.dattebayo.repository;

import com.joaoamg.dattebayo.model.UsuarioCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioClienteRepository extends JpaRepository<UsuarioCliente, UUID> {

    Optional<UsuarioCliente> findByEmail(String email);


    Optional<UsuarioCliente> findByCpf(String cpf);
}