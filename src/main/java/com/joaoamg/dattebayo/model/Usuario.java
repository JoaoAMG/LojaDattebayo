package com.joaoamg.dattebayo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @NonNull
    @Column(nullable = false)
    private String nome;

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String senha;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Boolean ativo;
    private String telefone;
    private String avatarUrl;
    private LocalDateTime ultimoLogin;
    private Boolean emailVerificado;
}