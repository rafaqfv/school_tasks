package com.example.schooltasks;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityAddTasksBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

public class AddTasksActivity extends AppCompatActivity {
    private ActivityAddTasksBinding binding;
    Calendar calendar;

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


        calendar = Calendar.getInstance();

        binding.salvarButton.setOnClickListener(v -> validateAndSaveTask());
        binding.data.setOnClickListener(v -> showDatePickerDialog());
    }

    private void validateAndSaveTask() {
        boolean isValid = true;

        // Discipline validation (example: required, minimum length)
        String disciplina = binding.disciplina.getText().toString().trim();
        if (disciplina.isEmpty()) {
            binding.disciplina.setError("Disciplina é obrigatória");
            isValid = false;
        } else {
            binding.disciplina.setError(null);
        }

        // Data validation
        String dataStr = binding.data.getText().toString().trim();
        LocalDate data = null;
        if (!isValidData(dataStr)) {
            binding.data.setError("Data inválida. Use o formato DD/MM/YYYY");
            isValid = false;
        } else {
            data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            binding.data.setError(null);
        }

        // Title validation (example: optional, maximum length)
        String titulo = binding.titulo.getText().toString().trim();

        // Description validation (example: no specific validation)
        String descricao = binding.descricao.getText().toString().trim();

        if (isValid) {
            saveTask(disciplina, data, titulo, descricao);
            clearInputsAndErrors();
        }
    }
    private void saveTask(String disciplina, LocalDate data, String titulo, String descricao) {

        Task newTask = new Task(disciplina, descricao, titulo, data);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newTask", newTask);
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    private void showDatePickerDialog() {
        // Defina a data mínima como o dia de hoje
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecionar Data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            updateDataEntregaEditText();
        });
        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private boolean isValidData(String data) {
        return data.matches("\\d{2}/\\d{2}/\\d{4}"); // Simple DD/MM/YYYY format check
    }

    private void updateDataEntregaEditText() {
        String dataStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        binding.data.setText(dataStr);
    }

    private void clearInputsAndErrors() {
        binding.disciplina.setText("");
        binding.data.setText("");
        binding.titulo.setText("");
        binding.descricao.setText("");
    }
}