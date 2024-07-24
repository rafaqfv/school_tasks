package com.example.schooltasks;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityAddTasksBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTasksActivity extends AppCompatActivity {
    private ActivityAddTasksBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTasksBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        binding.backBtn.setOnClickListener(v -> finish());

        datePicker();
        binding.salvarButton.setOnClickListener(v -> {
            if (validateFields()) {
                saveTask();
                clearInputsAndErrors();
            }
        });
    }

    private boolean validateFields() {
        String disciplina = binding.disciplina.getText().toString().trim();
        String dataStr = binding.data.getText().toString().trim();
        String titulo = binding.titulo.getText().toString().trim();
        String descricao = binding.descricao.getText().toString().trim();

        if (
                !titulo.isEmpty()
                        && isValidData(dataStr)
                        && !dataStr.isEmpty()
                        && !disciplina.isEmpty()
                        && !descricao.isEmpty()
        ) {
            return true;
        }

        if (descricao.isEmpty()) {
            binding.descricao.setError("Descrição é obrigatório");
        }

        if (titulo.isEmpty()) {
            binding.titulo.setError("Título é obrigatório");
        }

        if (disciplina.isEmpty()) {
            binding.disciplina.setError("Disciplina é obrigatória");
        }

        if (!isValidData(dataStr) || dataStr.isEmpty()) {
            binding.data.setError("Data inválida. Use o formato DD/MM/YYYY");
        }
        return false;
    }

    private void saveTask() {
        String disciplina = binding.disciplina.getText().toString().trim();
        String dataStr = binding.data.getText().toString().trim();
        String titulo = binding.titulo.getText().toString().trim();
        String descricao = binding.descricao.getText().toString().trim();

        Task newTask = new Task(disciplina, descricao, titulo, dataStr);
        addDocumentDB(newTask);
    }

    private void datePicker() {
        binding.data.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione uma data")
                    .build();

            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Obtém a data selecionada
                Date date = new Date(selection);

                // Formata a data para o formato dd/MM/yyyy
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = formatter.format(date);

                // Exibe a data formatada no TextView
                binding.data.setText(formattedDate);
            });
        });
    }

    private boolean isValidData(String data) {
        return data.matches("\\d{2}/\\d{2}/\\d{4}"); // Simple DD/MM/YYYY format check
    }

    private void clearInputsAndErrors() {
        binding.disciplina.setText("");
        binding.data.setText("");
        binding.titulo.setText("");
        binding.descricao.setText("");
    }

    private void addDocumentDB(Task task) {
        db.collection("tasks")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                            Toast.makeText(AddTasksActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                )
                .addOnFailureListener(e -> Toast.makeText(AddTasksActivity.this, "Falha", Toast.LENGTH_SHORT).show());
    }
}