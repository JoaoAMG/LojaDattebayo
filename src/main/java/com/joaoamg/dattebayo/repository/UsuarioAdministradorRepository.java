package com.joaoamg.dattebayo.repository;

import com.joaoamg.dattebayo.model.NivelAcesso;
import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioAdministradorRepository extends JpaRepository<UsuarioAdministrador, UUID> {

    Optional<UsuarioAdministrador> findByEmail(String email);


     List<UsuarioAdministrador> findByNivelAcesso(NivelAcesso nivelAcesso);
}
