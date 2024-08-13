package com.example.schooltasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltasks.OnItemClickListener;
import com.example.schooltasks.R;
import com.example.schooltasks.Turma;

import java.util.ArrayList;

public class TurmaAdapter extends RecyclerView.Adapter<TurmaAdapter.ViewHolder> {

    private ArrayList<Turma> turmaArrayList;
    private OnItemClickListener listener;

    public TurmaAdapter(ArrayList<Turma> turmaArrayList, OnItemClickListener listener) {
        this.turmaArrayList = turmaArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.turmas_card, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Turma turma = turmaArrayList.get(position);
        holder.nomeTurma.setText(turma.getNome());
        holder.nomeAdmin.setText(turma.getAdmin());
    }

    @Override
    public int getItemCount() {
        return (turmaArrayList != null) ? turmaArrayList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTurma, nomeAdmin;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nomeTurma = itemView.findViewById(R.id.nome_turma);
            nomeAdmin = itemView.findViewById(R.id.nomeAdmin);

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
