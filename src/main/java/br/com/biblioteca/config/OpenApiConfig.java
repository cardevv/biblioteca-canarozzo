package br.com.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Informacoes exibidas no topo do Swagger UI (/swagger-ui.html).
// O springdoc le os controllers e gera a documentacao em /v3/api-docs.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiBiblioteca() {
        return new OpenAPI().info(new Info()
                .title("API - Sistema de Biblioteca")
                .version("1.0.0")
                .description("""
                        API REST do projeto final de DSII: acervo (livros, autores, categorias), \
                        exemplares, empréstimos, reservas e consulta de ISBN na BrasilAPI.

                        Autenticação por sessão: faça login em /login.html (admin@biblioteca.com / admin123 \
                        ou leitor@biblioteca.com / leitor123) e depois use o Swagger na mesma aba do navegador.
                        Rotas de escrita exigem papel ADMIN."""));
    }
}
