package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.databinding.ActivityTasksBinding;
import com.example.schooltasks.adapter.TaskAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityTasksBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Task> taskList;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, this);

        binding.btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.intentAddTask.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, AddTasksActivity.class));
        });

        listenForTaskChanges();
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerTasks.setAdapter(adapter);
    }

    private void getItem() {

    }

    private void listenForTaskChanges() {
        db.collection("tasks")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    taskList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Task task = doc.toObject(Task.class);
                        task.setId(doc.getId());
                        taskList.add(task);

                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onItemClick(int position) {
        Task task = taskList.get(position);
        String disciplina, titulo, data, descricao, id;
        disciplina = task.getDisciplina();
        titulo = task.getTitulo();
        data = task.getDataDeEntrega();
        descricao = task.getDescricao();
        id = task.getId();

        Intent intent = new Intent(this, UpdateTask.class);
        intent.putExtra("disciplina", disciplina);
        intent.putExtra("titulo", titulo);
        intent.putExtra("data", data);
        intent.putExtra("descricao", descricao);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}