package br.com.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Exemplar = copia fisica de um livro. Um livro pode ter varios exemplares.
@Entity
@Table(name = "exemplar")
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Codigo de identificacao, ex.: "LIV1-EX1"
    @Column(nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    // Muitos exemplares para um livro -> coluna livro_id na tabela exemplar
    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    public Exemplar() {
    }

    public Exemplar(String codigo, Livro livro) {
        this.codigo = codigo;
        this.livro = livro;
        this.status = StatusExemplar.DISPONIVEL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public StatusExemplar getStatus() {
        return status;
    }

    public void setStatus(StatusExemplar status) {
        this.status = status;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }
}
