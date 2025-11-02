package com.joaoamg.dattebayo.config;

import com.joaoamg.dattebayo.model.*;
import com.joaoamg.dattebayo.repository.ProdutoRepository;
import com.joaoamg.dattebayo.repository.UsuarioAdministradorRepository;
import com.joaoamg.dattebayo.repository.UsuarioClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe de configuração para popular o banco de dados com dados iniciais (seeding).
 * Este código só será executado se o banco de dados estiver vazio.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            ProdutoRepository produtoRepository,
            UsuarioAdministradorRepository adminRepository,
            UsuarioClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (adminRepository.count() == 0 && clienteRepository.count() == 0) {
                System.out.println("Banco de dados vazio. Semeando dados iniciais...");

                // --- 1. Criar Utilizador Administrador Padrão (Completo) ---
                UsuarioAdministrador admin = new UsuarioAdministrador();
                admin.setNome("Admin Padrão");
                admin.setEmail("admin@dattebayo.com");
                admin.setSenha(passwordEncoder.encode("admin123")); // Senha é "admin123"
                admin.setNivelAcesso(NivelAcesso.SUPER);
                admin.setAtivo(true);
                admin.setEmailVerificado(true);
                admin.setTelefone("11987654321");
                admin.setDataCriacao(LocalDateTime.now());
                admin.setAvatarUrl("https://i.pravatar.cc/150?u=admin@dattebayo.com");
                adminRepository.save(admin);

                // --- 2. Criar Utilizador Cliente Padrão (Completo) ---
                Endereco enderecoCliente = Endereco.builder()
                        .logradouro("Rua das Cerejeiras")
                        .numero("106")
                        .cidade("Konoha")
                        .estado("País do Fogo")
                        .cep("12345-678")
                        .pais("País do Fogo")
                        .build();

                UsuarioCliente cliente = new UsuarioCliente();
                cliente.setNome("Sakura Haruno");
                cliente.setEmail("sakura@dattebayo.com");
                cliente.setSenha(passwordEncoder.encode("senhaforte123")); // Senha é "senhaforte123"
                cliente.setCpf("123.456.789-00");
                cliente.setEndereco(enderecoCliente);
                cliente.setAtivo(true);
                cliente.setEmailVerificado(true);
                cliente.setTelefone("11999998888");
                cliente.setDataCriacao(LocalDateTime.now());
                cliente.setAvatarUrl("https://i.pravatar.cc/150?u=sakura@dattebayo.com");
                clienteRepository.save(cliente);

                // --- 3. Criar Produtos Iniciais (Completos) ---
                Produto naruto1 = Produto.builder()
                        .nome("Naruto Vol. 1")
                        .autor("Masashi Kishimoto")
                        .editora("Panini")
                        .genero("Shonen")
                        .anoDeLancamento(2007)
                        .numeroDaEdicao(1)
                        .tipo("Mangá")
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
                        .numeroDaEdicao(1)
                        .tipo("Mangá")
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
                        .numeroDaEdicao(45)
                        .tipo("Mangá")
                        .valor(new BigDecimal("32.00"))
                        .imagemUrl("https://res.cloudinary.com/dce6vwgoa/image/upload/v1760822461/onepiece45.jpg")
                        .descricao("A saga de Thriller Bark chega a um clímax emocionante com a luta contra o gigante Oars.")
                        .build();

                produtoRepository.saveAll(List.of(naruto1, onepiece1, onepiece45));

                System.out.println("Dados iniciais (Admin, Cliente e Produtos) semeados com sucesso!");
            } else {
                System.out.println("O banco de dados já contém dados. A semeadura foi ignorada.");
            }
        };
    }
}