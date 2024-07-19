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
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {
    private ActivityTasksBinding binding;
    private FirebaseAuth mAuth;
    ArrayList<Task> taskList = new ArrayList<>();

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

        mAuth = FirebaseAuth.getInstance();

        binding.btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.intentAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTasksActivity.class);
        });

        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(taskList);
        binding.recyclerTasks.setAdapter(adapter);
    }
}