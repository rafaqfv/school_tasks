package com.example.schooltasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.disciplinaTextView.setText(task.getDisciplina());
        holder.dataTextView.setText(task.getDataDeEntrega());
        holder.tituloTextView.setText(task.getTitulo());
        holder.descricaoTextView.setText(task.getDescricao());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView disciplinaTextView, dataTextView, tituloTextView, descricaoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            disciplinaTextView = itemView.findViewById(R.id.disciplina);
            dataTextView = itemView.findViewById(R.id.data);
            tituloTextView = itemView.findViewById(R.id.titulo);
            descricaoTextView = itemView.findViewById(R.id.descricao);
        }
    }
}
