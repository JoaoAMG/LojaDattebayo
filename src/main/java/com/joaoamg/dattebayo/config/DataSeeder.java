package com.joaoamg.dattebayo.config;

import com.joaoamg.dattebayo.model.NivelAcesso;
import com.joaoamg.dattebayo.model.Produto;
import com.joaoamg.dattebayo.model.UsuarioAdministrador;
import com.joaoamg.dattebayo.repository.ProdutoRepository;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Classe de configuração para popular o banco de dados com dados iniciais .
 * Este código só será executado se o banco de dados estiver vazio.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(ProdutoRepository produtoRepository,
                                   UsuarioAdministradorRepository adminRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {

            if (produtoRepository.count() == 0 && adminRepository.count() == 0) {
                System.out.println("Banco de dados vazio. Semeando dados iniciais...");


                UsuarioAdministrador admin = new UsuarioAdministrador();
                admin.setNome("Admin Padrão");
                admin.setEmail("admin@dattebayo.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setNivelAcesso(NivelAcesso.SUPER);
                admin.setAtivo(true);
                adminRepository.save(admin);


                Produto naruto1 = Produto.builder()
                        .nome("Naruto Vol. 1")
                        .autor("Masashi Kishimoto")
                        .editora("Panini")
                        .genero("Shonen")
                        .anoDeLancamento(2007)
                        .valor(new BigDecimal("29.90"))
                        .imagemUrl("https://res.cloudinary.com/dce6vwgoa/image/upload/v1760821904/naruto1.jpg")
                        .descricao("A jornada de Naruto Uzumaki começa aqui! Acompanhe o ninja mais imprevisível de Konoha.")
                        .build();

                Produto onepiece1 = Produto.builder()
                        .nome("One Piece Vol. 1")
                        .autor("Eiichiro Oda")
                        .editora("Panini")
                        .genero("Shonen")
                        .anoDeLancamento(2012)
                        .valor(new BigDecimal("27.50"))
                        .imagemUrl("https://res.cloudinary.com/dce6vwgoa/image/upload/v1760822329/onepiece1.jpg")
                        .descricao("O início da maior aventura dos mares! Monkey D. Luffy parte em busca do lendário tesouro One Piece.")
                        .build();

                Produto onepiece45 = Produto.builder()
                        .nome("One Piece Vol. 45")
                        .autor("Eiichiro Oda")
                        .editora("Panini")
                        .genero("Shonen")
                        .anoDeLancamento(2015)
                        .valor(new BigDecimal("32.00"))
                        .imagemUrl("https://res.cloudinary.com/dce6vwgoa/image/upload/v1760822461/onepiece45.jpg")
                        .descricao("A saga de Thriller Bark chega a um clímax emocionante com a luta contra o gigante Oars.")
                        .build();


                produtoRepository.saveAll(List.of(naruto1, onepiece1, onepiece45));

                System.out.println("Dados iniciais semeados com sucesso!");
            } else {
                System.out.println("O banco de dados já contém dados. A semeadura foi ignorada.");
            }
        };
    }
}