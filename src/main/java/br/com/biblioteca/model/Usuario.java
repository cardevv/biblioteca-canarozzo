package br.com.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Entidade JPA: vira a tabela "usuario" no banco
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id gerado pelo banco
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true) // nao pode haver dois usuarios com o mesmo email
    private String email;

    // Guarda o hash BCrypt, nunca a senha em texto puro
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING) // grava "ADMIN"/"LEITOR" em vez de 0/1
    @Column(nullable = false)
    private Papel papel;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, Papel papel) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.papel = papel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }
}
