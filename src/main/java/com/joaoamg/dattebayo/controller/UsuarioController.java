package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.service.UsuarioAdministradorService;
import com.joaoamg.dattebayo.service.UsuarioClienteService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class UsuarioController {

    private final UsuarioClienteService clienteService;
    private final UsuarioAdministradorService administradorService;

    public UsuarioController(UsuarioClienteService clienteService, UsuarioAdministradorService administradorService) {
        this.clienteService = clienteService;
        this.administradorService = administradorService;
    }

    @QueryMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR')")
    public UsuarioCliente clientePorEmail(@Argument String email) {
        return clienteService.buscarPorEmail(email);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR') or authentication.principal.id == #clienteInput.id")
    public UsuarioCliente atualizarCliente(@Argument("clienteInput") UsuarioCliente clienteInput) {
        return clienteService.atualizar(clienteInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public UUID deletarCliente(@Argument UUID id) {
        clienteService.deletar(id);
        return id;
    }

    @MutationMapping
    @PreAuthorize("isAnonymous()")
    public UsuarioAdministrador registrarAdministrador(@Argument("adminInput") UsuarioAdministrador adminInput) {
        return administradorService.registrarAdministrador(adminInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SUPER') or authentication.principal.id == #adminInput.id")
    public UsuarioAdministrador atualizarAdministrador(@Argument("adminInput") UsuarioAdministrador adminInput) {
        return administradorService.atualizar(adminInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public UUID deletarAdministrador(@Argument UUID id) {
        administradorService.deletar(id);
        return id;
    }
}

