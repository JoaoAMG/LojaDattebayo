package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Endereco;
import com.joaoamg.dattebayo.repository.EnderecoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }


    @Transactional
    public Endereco criar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }


    @Transactional
    public Optional<Endereco> buscarPorId(UUID id) {
        return enderecoRepository.findById(id);
    }
    @Transactional
    public List<Endereco> buscarTodos() {
        return enderecoRepository.findAll();
    }


    @Transactional
    public Endereco atualizar(Endereco endereco) {
        if (endereco.getId() == null || !enderecoRepository.existsById(endereco.getId())) {
            // ✅ Usando exceção customizada
            throw new ResourceNotFoundException("Endereço", "ID", endereco.getId());
        }
        return enderecoRepository.save(endereco);
    }


    @Transactional
    public void deletar(UUID id) {
        if (!enderecoRepository.existsById(id)) {
            // ✅ Usando exceção customizada
            throw new ResourceNotFoundException("Endereço", "ID", id);
        }
        enderecoRepository.deleteById(id);
    }
}