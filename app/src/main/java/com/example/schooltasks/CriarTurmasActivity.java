package com.example.schooltasks;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.adapter.Turma;
import com.example.schooltasks.databinding.ActivityCriarTurmasBinding;
import com.example.schooltasks.databinding.ActivityTurmasBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class CriarTurmasActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ActivityCriarTurmasBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCriarTurmasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backBtn.setOnClickListener(v -> finish());

        binding.salvarBtn.setOnClickListener(v -> {
            // TODO: 11/08/2024
            validateFields();
        });
    }

    private Turma validateFields() {
        String nome = binding.nomeInput.getText().toString().trim();
        String curso = binding.cursoInput.getText().toString().trim();

        if (!nome.isEmpty() && !curso.isEmpty()) {
            criarTurma(new Turma(nome, curso));
        }

        if (nome.isEmpty()) {
            Toast.makeText(this, "Nome está vazio", Toast.LENGTH_SHORT).show();
            binding.nomeLayout.setError("Nome é obrigatório");
        }

        if (curso.isEmpty()) {
            Toast.makeText(this, "Curso está vazio", Toast.LENGTH_SHORT).show();
            binding.cursoLayout.setError("Curso é obrigatório");
        }

        return null;
    }

    private void criarTurma(Turma turma) {
        db.collection("turma")
                .add(turma)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Falha", Toast.LENGTH_SHORT).show());
    }
}