package br.com.biblioteca.controller;

import br.com.biblioteca.model.Exemplar;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.repository.AutorRepository;
import br.com.biblioteca.repository.CategoriaRepository;
import br.com.biblioteca.repository.ExemplarRepository;
import br.com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Teste de INTEGRACAO: sobe a aplicacao inteira (Spring + Security + JPA + H2)
// e faz requisicoes HTTP simuladas com o MockMvc.
// A carga inicial (CargaInicial) roda antes, entao ja existem livros e usuarios.
// @Transactional desfaz as alteracoes no banco ao fim de cada teste.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Test
    @WithMockUser(roles = "LEITOR") // simula um usuario logado com papel LEITOR
    void leitorListaLivros() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").exists());
    }

    @Test
    @WithMockUser(roles = "LEITOR")
    void leitorNaoPodeCriarLivro() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNovoLivro()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCriaLivro() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNovoLivro()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Livro de Teste"))
                .andExpect(jsonPath("$.isbn").value("9990000000001"));
    }

    @Test
    void semLoginRecebe401() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "leitor@biblioteca.com", roles = "LEITOR")
    void naoPermiteReservarLivroComExemplarDisponivel() throws Exception {
        // Cria um exemplar DISPONIVEL para um livro: a reserva deve ser recusada
        Livro livro = livroRepository.findAll().get(0);
        exemplarRepository.save(new Exemplar("TESTE-EX1", livro));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"livroId\": " + livro.getId() + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("O livro possui exemplar disponível; não é necessário reservar"));
    }

    @Test
    @WithMockUser(username = "leitor@biblioteca.com", roles = "LEITOR")
    void leitorReservaLivroSemExemplarDisponivel() throws Exception {
        // Os livros da carga inicial nao tem exemplares, entao a reserva e aceita
        Livro livro = livroRepository.findAll().get(0);

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"livroId\": " + livro.getId() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    // JSON de um livro novo usando uma categoria e um autor da carga inicial
    private String jsonNovoLivro() {
        Long categoriaId = categoriaRepository.findAll().get(0).getId();
        Long autorId = autorRepository.findAll().get(0).getId();
        return """
                {"titulo": "Livro de Teste", "isbn": "9990000000001", "editora": "Editora X",
                 "anoPublicacao": 2024, "categoriaId": %d, "autorIds": [%d]}
                """.formatted(categoriaId, autorId);
    }
}
