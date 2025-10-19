package com.joaoamg.dattebayo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private Integer numeroDaEdicao;
    private String autor;
    private String editora;
    private String genero;
    private Integer anoDeLancamento;
    private BigDecimal valor;
    private String tipo;


    @Column(length = 1024)
    private String imagemUrl;

    @Column(length = 2048)
    private String descricao;
}
