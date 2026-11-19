package br.com.biblioteca.service;

import br.com.biblioteca.dto.DevolucaoResponse;
import br.com.biblioteca.dto.EmprestimoRequest;
import br.com.biblioteca.dto.EmprestimoResponse;
import br.com.biblioteca.exception.RegraNegocioException;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Exemplar;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Papel;
import br.com.biblioteca.model.StatusEmprestimo;
import br.com.biblioteca.model.StatusExemplar;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.ExemplarRepository;
import br.com.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Teste UNITARIO: testa so o EmprestimoService. Os repositories sao "mocks"
// (objetos falsos do Mockito), entao nao ha banco de dados nem Spring aqui.
@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private ExemplarRepository exemplarRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    // Cria o EmprestimoService real, injetando os mocks acima no construtor
    @InjectMocks
    private EmprestimoService emprestimoService;

    private Usuario leitor;
    private Exemplar exemplar;

    @BeforeEach
    void prepararDados() {
        leitor = new Usuario("Leitor", "leitor@teste.com", "hash", Papel.LEITOR);
        leitor.setId(1L);

        Livro livro = new Livro();
        livro.setId(10L);
        livro.setTitulo("Dom Casmurro");

        exemplar = new Exemplar("LIV10-EX1", livro);
        exemplar.setId(100L);
    }

    @Test
    void deveRealizarEmprestimoComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(exemplarRepository.findById(100L)).thenReturn(Optional.of(exemplar));
        when(emprestimoRepository.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(0L);
        // save devolve o proprio objeto recebido
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> inv.getArgument(0));

        EmprestimoResponse resposta = emprestimoService.emprestar(new EmprestimoRequest(1L, 100L));

        assertEquals(StatusEmprestimo.ATIVO, resposta.status());
        assertEquals(LocalDate.now(), resposta.dataEmprestimo());
        assertEquals(LocalDate.now().plusDays(14), resposta.dataPrevistaDevolucao());
        assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
        verify(emprestimoRepository).save(any(Emprestimo.class));
    }

    @Test
    void naoDeveEmprestarExemplarIndisponivel() {
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(exemplarRepository.findById(100L)).thenReturn(Optional.of(exemplar));

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> emprestimoService.emprestar(new EmprestimoRequest(1L, 100L)));

        assertEquals("O exemplar LIV10-EX1 não está disponível", erro.getMessage());
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    void naoDeveEmprestarQuandoLimiteDeTresAtingido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(leitor));
        when(exemplarRepository.findById(100L)).thenReturn(Optional.of(exemplar));
        when(emprestimoRepository.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(3L);

        assertThrows(RegraNegocioException.class,
                () -> emprestimoService.emprestar(new EmprestimoRequest(1L, 100L)));

        verify(emprestimoRepository, never()).save(any());
        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
    }

    @Test
    void devolucaoNoPrazoNaoGeraMulta() {
        Emprestimo emprestimo = emprestimoAtivo(LocalDate.now().plusDays(3)); // ainda faltam 3 dias
        when(emprestimoRepository.findById(50L)).thenReturn(Optional.of(emprestimo));

        DevolucaoResponse resposta = emprestimoService.devolver(50L);

        assertEquals(0, resposta.diasAtraso());
        assertEquals(0, BigDecimal.ZERO.compareTo(resposta.valorMulta()));
        assertEquals(StatusEmprestimo.DEVOLVIDO, emprestimo.getStatus());
        assertEquals(LocalDate.now(), emprestimo.getDataDevolucao());
        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
    }

    @Test
    void devolucaoComCincoDiasDeAtrasoGeraMultaDeDezReais() {
        Emprestimo emprestimo = emprestimoAtivo(LocalDate.now().minusDays(5)); // venceu ha 5 dias
        when(emprestimoRepository.findById(50L)).thenReturn(Optional.of(emprestimo));

        DevolucaoResponse resposta = emprestimoService.devolver(50L);

        assertEquals(5, resposta.diasAtraso());
        assertEquals(0, new BigDecimal("10.00").compareTo(resposta.valorMulta()));
        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
    }

    // Monta um emprestimo ATIVO com a data prevista informada
    private Emprestimo emprestimoAtivo(LocalDate dataPrevista) {
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(50L);
        emprestimo.setUsuario(leitor);
        emprestimo.setExemplar(exemplar);
        emprestimo.setDataEmprestimo(dataPrevista.minusDays(14));
        emprestimo.setDataPrevistaDevolucao(dataPrevista);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        return emprestimo;
    }
}
