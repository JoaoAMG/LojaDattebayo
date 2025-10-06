package com.joaoamg.dattebayo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class UsuarioCliente extends Usuario {

    private String cpf;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;


    public UsuarioCliente(String nome, String email, String senha, String cpf, Endereco endereco) {
        super(nome, email, senha);
        this.cpf = cpf;
        this.endereco = endereco;
    }
}