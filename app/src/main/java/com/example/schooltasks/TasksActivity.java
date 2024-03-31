package com.example.schooltasks;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.databinding.ActivityTasksBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {
    private ActivityTasksBinding binding;
    private static final int REQUEST_ADD_TASK = 1;
    List<Task> taskList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        binding.intentAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTasksActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });

        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(this));

        TaskAdapter adapter = new TaskAdapter(taskList);
        binding.recyclerTasks.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TASK && resultCode == RESULT_OK && data != null) {
            // Receba os dados da nova tarefa da AddTasksActivity
            Task newTask = data.getParcelableExtra("newTask");
            if (newTask != null) {
                // Adicione a nova tarefa ao RecyclerView
                taskList.add(newTask);
                TaskAdapter adapter = new TaskAdapter(taskList);
                binding.recyclerTasks.setAdapter(adapter);
            }
        }
    }

}