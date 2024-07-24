package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityUpdateTaskBinding;

public class UpdateTask extends AppCompatActivity {
    private ActivityUpdateTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUpdateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String disciplina = intent.getStringExtra("disciplina");
        String titulo = intent.getStringExtra("titulo");
        String dataDeEntrega = intent.getStringExtra("data");
        String descricao = intent.getStringExtra("descricao");
        String id = intent.getStringExtra("id");




    }

    public void updateTask() {



    }

}