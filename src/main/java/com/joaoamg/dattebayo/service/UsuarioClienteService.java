package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.UsuarioCliente;
import com.joaoamg.dattebayo.repository.UsuarioClienteRepository;
import com.joaoamg.dattebayo.erros.BusinessRuleException;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List; // ✅ Import Adicionado
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioClienteService {

    private final UsuarioClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioClienteService(UsuarioClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioCliente registrarCliente(UsuarioCliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new BusinessRuleException("O e-mail '" + cliente.getEmail() + "' já está cadastrado.");
        }
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        cliente.setDataCriacao(LocalDateTime.now());
        cliente.setAtivo(true);
        cliente.setEmailVerificado(false);
        return clienteRepository.save(cliente);
    }

    public UsuarioCliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "e-mail", email));
    }

    public Optional<UsuarioCliente> buscarPorId(UUID id) {
        return clienteRepository.findById(id);
    }

    /**
     * ✅ NOVO MÉTODO: Retorna uma lista de todos os clientes.
     */
    public List<UsuarioCliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional
    public UsuarioCliente atualizar(UsuarioCliente cliente) {
        if (cliente.getId() == null) {
            throw new BusinessRuleException("ID do Cliente é obrigatório para atualização.");
        }
        UsuarioCliente clienteExistente = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "ID", cliente.getId()));

        if (cliente.getSenha() != null && !cliente.getSenha().isEmpty()) {
            cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        } else {
            cliente.setSenha(clienteExistente.getSenha());
        }
        cliente.setDataAtualizacao(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", "ID", id);
        }
        clienteRepository.deleteById(id);
    }
}