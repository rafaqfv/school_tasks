package com.example.schooltasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltasks.Class.Aluno;
import com.example.schooltasks.OnItemClickListener;
import com.example.schooltasks.R;


import java.util.ArrayList;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.ViewHolder> {
    private final ArrayList<Aluno> listaAlunos;
    private OnItemClickListener listener;

    public AlunoAdapter(ArrayList<Aluno> listaAlunos, OnItemClickListener listener) {
        this.listaAlunos = listaAlunos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlunoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alunos_card, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlunoAdapter.ViewHolder holder, int position) {
        Aluno aluno = listaAlunos.get(position);
        holder.nomeAluno.setText(aluno.getNome());
        holder.emailAluno.setText(aluno.getEmail());
    }

    @Override
    public int getItemCount() {
        return listaAlunos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeAluno;
        public TextView emailAluno;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nomeAluno = itemView.findViewById(R.id.nomeAluno);
            emailAluno = itemView.findViewById(R.id.emailAluno);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
