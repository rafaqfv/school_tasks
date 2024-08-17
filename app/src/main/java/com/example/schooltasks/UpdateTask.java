package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityUpdateTaskBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class UpdateTask extends AppCompatActivity {
    private ActivityUpdateTaskBinding binding;
    private FirebaseFirestore db;
    private Intent intent;
    private String id;

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
        db = FirebaseFirestore.getInstance();
        binding.backBtn.setOnClickListener(v -> finish());
        intent = getIntent();
        updateFields();
        datePicker();
        binding.salvarButton.setOnClickListener(v -> updateTask(id));
        binding.excluirBtn.setOnClickListener(v -> deleteTask(id));
    }

    private void deleteTask(String id) {
        db.collection("tasks").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tarefa excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir tarefa.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateTask(String id) {
        // Obtenha os dados atualizados das views
        String updatedDisciplina = binding.disciplina.getText().toString();
        String updatedTitulo = binding.titulo.getText().toString();
        String updatedDataDeEntrega = binding.data.getText().toString();
        String updatedDescricao = binding.descricao.getText().toString();

        if (updatedDisciplina.isEmpty() || updatedTitulo.isEmpty() || updatedDataDeEntrega.isEmpty() || updatedDescricao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> task = new HashMap<>();
        task.put("disciplina", updatedDisciplina);
        task.put("titulo", updatedTitulo);
        task.put("dataDeEntrega", updatedDataDeEntrega);
        task.put("descricao", updatedDescricao);

        db.collection("tasks").document(id)
                .update(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar tarefa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void datePicker() {
        binding.data.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione uma data")
                    .build();
            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Date date = new Date(selection);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formattedDate = formatter.format(date);
                binding.data.setText(formattedDate);
            });
        });
    }

    private void updateFields() {
        String disciplina = intent.getStringExtra("disciplina");
        String titulo = intent.getStringExtra("titulo");
        String dataDeEntrega = intent.getStringExtra("data");
        String descricao = intent.getStringExtra("descricao");
        id = intent.getStringExtra("id");

        binding.data.setText(dataDeEntrega);
        binding.disciplina.setText(disciplina);
        binding.titulo.setText(titulo);
        binding.descricao.setText(descricao);
    }
}