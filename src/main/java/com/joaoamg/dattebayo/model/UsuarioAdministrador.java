package com.joaoamg.dattebayo.model;
import com.joaoamg.dattebayo.model.NivelAcesso;
import com.joaoamg.dattebayo.model.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class UsuarioAdministrador extends Usuario {

    @Enumerated(EnumType.STRING)
    private NivelAcesso nivelAcesso;

    public UsuarioAdministrador(String nome, String email, String senha, NivelAcesso nivelAcesso) {
        super(nome, email, senha);
        this.nivelAcesso = nivelAcesso;
    }
}