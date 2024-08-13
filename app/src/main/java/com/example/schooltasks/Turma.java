package com.example.schooltasks;

public class Turma {
    private String nome;
    private String id;
    private String admin;
    private String nomeAdmin;

    public String getNomeAdmin() {
        return nomeAdmin;
    }

    public void setNomeAdmin(String nomeAdmin) {
        this.nomeAdmin = nomeAdmin;
    }

    public Turma() {
    }

    public Turma(String nome, String admin, String nomeAdmin) {
        this.nome = nome;
        this.admin = admin;
        this.nomeAdmin = nomeAdmin;
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
