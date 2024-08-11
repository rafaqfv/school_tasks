package com.example.schooltasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltasks.OnItemClickListener;
import com.example.schooltasks.R;

import java.util.ArrayList;

public class TurmaAdapter extends RecyclerView.Adapter<TurmaAdapter.ViewHolder> {

    ArrayList<Turma> turmaArrayList;
    private OnItemClickListener listener;

    public TurmaAdapter(ArrayList<Turma> turmaArrayList, OnItemClickListener listener) {
        this.turmaArrayList = turmaArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TurmaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.turmas_card, parent, false);
        return new TurmaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TurmaAdapter.ViewHolder holder, int position) {
        Turma turma = turmaArrayList.get(position);
        holder.bind(turma, listener);
    }

    @Override
    public int getItemCount() {
        return turmaArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeTurma;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTurma = itemView.findViewById(R.id.nome_turma);
        }

        public void bind(final Turma turma, final OnItemClickListener listener) {
            nomeTurma.setText(turma.getNome());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
