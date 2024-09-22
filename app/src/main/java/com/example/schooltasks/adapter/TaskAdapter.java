package com.example.schooltasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltasks.OnItemClickListener;
import com.example.schooltasks.R;
import com.example.schooltasks.Class.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks;
    private OnItemClickListener listener;

    public TaskAdapter(ArrayList<Task> tasks, OnItemClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.disciplinaTextView.setText(task.getDisciplina());
        holder.dataTextView.setText(Task.formatarData(task.getDataDeEntrega()));
        holder.tituloTextView.setText(task.getTitulo());
        holder.descricaoTextView.setText(task.getDescricao());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView disciplinaTextView, dataTextView, tituloTextView, descricaoTextView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            disciplinaTextView = itemView.findViewById(R.id.disciplina);
            dataTextView = itemView.findViewById(R.id.data);
            tituloTextView = itemView.findViewById(R.id.titulo);
            descricaoTextView = itemView.findViewById(R.id.descricao);

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