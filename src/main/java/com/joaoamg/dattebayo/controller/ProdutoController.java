package com.joaoamg.dattebayo.controller;

import com.joaoamg.dattebayo.erros.ResourceNotFoundException;
import com.joaoamg.dattebayo.model.Produto;
import com.joaoamg.dattebayo.service.ProdutoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@Controller
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @QueryMapping
    @PreAuthorize("permitAll") // Permitir que todos vejam os produtos
    public List<Produto> produtos() {
        return produtoService.buscarTodos();
    }

    @QueryMapping
    @PreAuthorize("permitAll")
    public Produto produto(@Argument UUID id) {
        return produtoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "ID", id));
    }

    @QueryMapping
    @PreAuthorize("permitAll")
    public List<Produto> produtosPorGenero(@Argument String genero) {
        return produtoService.buscarPorGenero(genero);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR')")
    public Produto criarProduto(@Argument("produtoInput") Produto produtoInput) {
        return produtoService.criar(produtoInput);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'MODERADOR')")
    public Produto atualizarProduto(@Argument UUID id, @Argument("produtoInput") Produto produtoInput) {
        return produtoService.atualizar(id, produtoInput);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SUPER')")
    public UUID deletarProduto(@Argument UUID id) {
        produtoService.deletar(id);
        return id;
    }
}
