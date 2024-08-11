package com.example.schooltasks;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.adapter.TurmaAdapter;
import com.example.schooltasks.databinding.ActivityTurmasBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class TurmasActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ActivityTurmasBinding binding;
    private TurmaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTurmasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.turmasRecycler.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        adapter = new TurmaAdapter(this);
        binding.turmasRecycler.setAdapter(adapter);

        binding.backBtn.setOnClickListener(v -> finish());


    }
}