# Sistema de Biblioteca

Projeto final de DSII: sistema web para gerenciar o acervo de uma biblioteca (livros, autores,
categorias e exemplares), empréstimos com cálculo de multa por atraso e reservas de livros.
Tem login com dois perfis (ADMIN e LEITOR), API REST documentada com Swagger e consulta
de livros por ISBN na BrasilAPI.

## Integrantes

| Nome | GitHub |
|---|---|
| Carlos | [@cardevv](https://github.com/cardevv) |
| Milla | [@milla-alt](https://github.com/milla-alt) |
| Caio | [@caiolikk](https://github.com/caiolikk) |
| Ana | [@ananinazx](https://github.com/ananinazx) |

## Tecnologias

- Java 21, Spring Boot 3, Maven
- Spring Web, Spring Data JPA (Hibernate), Spring Security, Bean Validation
- H2 (desenvolvimento) e PostgreSQL (produção)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito e MockMvc
- HTML, CSS e JavaScript puro (fetch)
- Docker (deploy)

## Como executar

Pré-requisito: Java 21 ou mais recente.

```bash
mvn spring-boot:run
# ou, sem Maven instalado:
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

Acesse <http://localhost:8080> e entre com um dos usuários de teste:

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | admin@biblioteca.com | admin123 |
| LEITOR | leitor@biblioteca.com | leitor123 |

Para rodar os testes: `mvn test`

## Links

- Aplicação publicada: [https://SEU-APP.onrender.com](https://SEU-APP.onrender.com)
- Swagger: [https://SEU-APP.onrender.com/swagger-ui.html](https://SEU-APP.onrender.com/swagger-ui.html) (local: <http://localhost:8080/swagger-ui.html>)
