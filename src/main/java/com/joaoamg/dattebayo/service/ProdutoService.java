package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Produto;
import com.joaoamg.dattebayo.repository.ProdutoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }


    public Produto criar(Produto produto) {

        return produtoRepository.save(produto);
    }


    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(UUID id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> buscarPorGenero(String genero) {

        return produtoRepository.findByGenero(genero);
    }


    public Produto atualizar(Produto produto) {
        if (produto.getId() == null || !produtoRepository.existsById(produto.getId())) {

            throw new ResourceNotFoundException("Produto", "ID", produto.getId());
        }
        return produtoRepository.save(produto);
    }


    public void deletar(UUID id) {
        if (!produtoRepository.existsById(id)) {

            throw new ResourceNotFoundException("Produto", "ID", id);
        }
        produtoRepository.deleteById(id);
    }
}