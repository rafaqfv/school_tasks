package com.example.schooltasks;

public class Task {

    private String disciplina;
    private String descricao;
    private String titulo;
    private String dataDeEntrega;

    public Task() {

    }

    public Task(String disciplina, String descricao, String titulo, String dataDeEntrega) {
        this.disciplina = disciplina;
        this.descricao = descricao;
        this.titulo = titulo;
        this.dataDeEntrega = dataDeEntrega;
//        this.dataDeEntrega = Task.converterParaPortugues(dataLocalDate.getDayOfWeek());
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

    public String getDataDeEntrega() {
        return dataDeEntrega;
    }

    public void setDataDeEntrega(String dataDeEntrega) {
        this.dataDeEntrega = dataDeEntrega;
    }

//    public static String converterParaPortugues(DayOfWeek diaDaSemana) {
//        switch (diaDaSemana) {
//            case MONDAY:
//                return "Segunda-feira";
//            case TUESDAY:
//                return "Terça-feira";
//            case WEDNESDAY:
//                return "Quarta-feira";
//            case THURSDAY:
//                return "Quinta-feira";
//            case FRIDAY:
//                return "Sexta-feira";
//            case SATURDAY:
//                return "Sábado";
//            case SUNDAY:
//                return "Domingo";
//            default:
//                return "";
//        }
//    }
}
