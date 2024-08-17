package com.example.schooltasks;

public class Aluno {
    private String nome;
    private String email;
    private String id;

    public Aluno(String nome, String email, String id) {
        this.nome = nome;
        this.email = email;
        this.id = id;
    }

    public Aluno() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
