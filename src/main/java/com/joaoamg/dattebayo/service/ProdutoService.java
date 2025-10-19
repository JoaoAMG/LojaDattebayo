package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.Produto;
import com.joaoamg.dattebayo.repository.ProdutoRepository;
import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
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


    @Transactional
    public Produto atualizar(UUID id, Produto produtoInput) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "ID", id));

        produtoExistente.setNome(produtoInput.getNome());
        produtoExistente.setNumeroDaEdicao(produtoInput.getNumeroDaEdicao());
        produtoExistente.setAutor(produtoInput.getAutor());
        produtoExistente.setEditora(produtoInput.getEditora());
        produtoExistente.setGenero(produtoInput.getGenero());
        produtoExistente.setAnoDeLancamento(produtoInput.getAnoDeLancamento());
        produtoExistente.setValor(produtoInput.getValor());
        produtoExistente.setTipo(produtoInput.getTipo());
        produtoExistente.setImagemUrl(produtoInput.getImagemUrl());
        produtoExistente.setDescricao(produtoInput.getDescricao());

        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto", "ID", id);
        }
        produtoRepository.deleteById(id);
    }
}
