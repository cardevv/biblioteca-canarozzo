package br.com.biblioteca.repository;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Quantos emprestimos ATIVOS o usuario tem (regra do maximo de 3)
    long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
}
