package com.example.schooltasks;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Task implements Parcelable {

    private String disciplina;
    private String descricao;
    private String titulo;
    private String dataDeEntrega;

    public Task(String disciplina, String descricao, String titulo, LocalDate dataLocalDate) {
        this.disciplina = disciplina;
        this.descricao = descricao;
        this.titulo = titulo;
        this.dataDeEntrega = Task.converterParaPortugues(dataLocalDate.getDayOfWeek());
    }

    protected Task(Parcel in) {
        disciplina = in.readString();
        descricao = in.readString();
        titulo = in.readString();
        dataDeEntrega = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(disciplina);
        dest.writeString(descricao);
        dest.writeString(titulo);
        dest.writeString(dataDeEntrega);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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

    public static String converterParaPortugues(DayOfWeek diaDaSemana) {
        switch (diaDaSemana) {
            case MONDAY:
                return "Segunda-feira";
            case TUESDAY:
                return "Terça-feira";
            case WEDNESDAY:
                return "Quarta-feira";
            case THURSDAY:
                return "Quinta-feira";
            case FRIDAY:
                return "Sexta-feira";
            case SATURDAY:
                return "Sábado";
            case SUNDAY:
                return "Domingo";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "disciplina='" + disciplina + '\'' +
                ", descricao='" + descricao + '\'' +
                ", titulo='" + titulo + '\'' +
                ", dataDeEntrega='" + dataDeEntrega + '\'' +
                '}';
    }
}
