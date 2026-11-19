package br.com.biblioteca.repository;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Quantos emprestimos ATIVOS o usuario tem (regra do maximo de 3)
    long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    List<Emprestimo> findAllByOrderByIdDesc();

    List<Emprestimo> findByStatusOrderByIdDesc(StatusEmprestimo status);

    // Navega no relacionamento: emprestimo.usuario.email
    List<Emprestimo> findByUsuarioEmailOrderByIdDesc(String email);
}
