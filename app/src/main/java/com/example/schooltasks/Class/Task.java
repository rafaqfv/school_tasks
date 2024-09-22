package com.example.schooltasks.Class;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Task {

    private String disciplina;
    private String descricao;
    private String titulo;
    private Timestamp dataDeEntrega;
    private String id;

    public Task() {

    }

    public Task(String disciplina, String descricao, String titulo, String dataDeEntrega, String idTurma) {
        this.disciplina = disciplina;
        this.descricao = descricao;
        this.titulo = titulo;
        this.dataDeEntrega = converterParaTimestamp(dataDeEntrega);
        this.idTurma = idTurma;
    }

    public String getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(String idTurma) {
        this.idTurma = idTurma;
    }

    private String idTurma;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Timestamp getDataDeEntrega() {
        return dataDeEntrega;
    }

    public void setDataDeEntrega(Timestamp dataDeEntrega) {
        this.dataDeEntrega = dataDeEntrega;
    }

    public static String formatarData(Timestamp data){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dataFormatada = sdf.format(data.toDate());
        return dataFormatada;
    }

    public static Timestamp converterParaTimestamp(String dataStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date parsedDate = dateFormat.parse(dataStr);
            return new Timestamp(parsedDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
