package com.example.schooltasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltasks.classes.Aluno;
import com.example.schooltasks.OnItemClickListener;
import com.example.schooltasks.R;


import java.util.ArrayList;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.ViewHolder> {
    private final ArrayList<Aluno> listaAlunos;
    private OnItemClickListener listener;
    private ArrayList<String> admins;

    public AlunoAdapter(ArrayList<Aluno> listaAlunos, OnItemClickListener listener, ArrayList<String> admins) {
        this.listaAlunos = listaAlunos;
        this.listener = listener;
        this.admins = admins;
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

        if (admins.contains(aluno.getId())) holder.adminIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listaAlunos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeAluno;
        public TextView emailAluno;
        public ImageView adminIcon;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nomeAluno = itemView.findViewById(R.id.nomeAluno);
            emailAluno = itemView.findViewById(R.id.emailAluno);
            adminIcon = itemView.findViewById(R.id.isAdmin);

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
