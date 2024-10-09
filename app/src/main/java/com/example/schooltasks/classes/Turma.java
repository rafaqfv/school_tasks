package com.example.schooltasks.classes;

public class Turma {
    private String nome;
    private String id;
    private String admin;
    private String nomeCriador;

    public String getNomeCriador() {
        return nomeCriador;
    }

    public void setNomeCriador(String nomeCriador) {
        this.nomeCriador = nomeCriador;
    }

    public Turma() {
    }

    public Turma(String nome, String admin, String nomeAdmin) {
        this.nome = nome;
        this.admin = admin;
        this.nomeCriador = nomeAdmin;
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
