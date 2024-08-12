package com.example.schooltasks.adapter;

public class Turma {
    private String nome;
    private String id;
    private String admin;

    public Turma() {
    }

    public Turma(String nome, String admin) {
        this.nome = nome;
        this.admin = admin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
