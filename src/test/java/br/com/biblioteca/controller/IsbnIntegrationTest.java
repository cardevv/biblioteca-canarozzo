package br.com.biblioteca.controller;

import br.com.biblioteca.client.IsbnClient;
import br.com.biblioteca.dto.IsbnResponse;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Testa o endpoint /api/isbn SEM chamar a BrasilAPI de verdade:
// @MockitoBean troca o IsbnClient do contexto do Spring por um mock.
@SpringBootTest
@AutoConfigureMockMvc
class IsbnIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IsbnClient isbnClient;

    @Test
    @WithMockUser(roles = "ADMIN")
    void consultaIsbnRetornaDadosDoLivro() throws Exception {
        when(isbnClient.buscarPorIsbn("9788535914849"))
                .thenReturn(new IsbnResponse("1984", List.of("George Orwell"), "Companhia das Letras", 2009));

        mockMvc.perform(get("/api/isbn/9788535914849"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.authors[0]").value("George Orwell"))
                .andExpect(jsonPath("$.publisher").value("Companhia das Letras"))
                .andExpect(jsonPath("$.year").value(2009));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void isbnNaoEncontradoRetorna404() throws Exception {
        when(isbnClient.buscarPorIsbn("0000000000"))
                .thenThrow(new RecursoNaoEncontradoException("ISBN não encontrado: 0000000000"));

        mockMvc.perform(get("/api/isbn/0000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ISBN não encontrado: 0000000000"));
    }

    @Test
    @WithMockUser(roles = "LEITOR")
    void leitorNaoPodeConsultarIsbn() throws Exception {
        mockMvc.perform(get("/api/isbn/9788535914849"))
                .andExpect(status().isForbidden());
    }
}
