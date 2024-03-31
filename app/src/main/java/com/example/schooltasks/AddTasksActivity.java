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
        binding.dataLayout.setOnClickListener(v -> showDatePickerDialog());
    }

    private void validateAndSaveTask() {
        boolean isValid = true;

        // Discipline validation (example: required, minimum length)
        String disciplina = binding.disciplina.getText().toString().trim();
        if (disciplina.isEmpty()) {
            binding.disciplina.setError("Disciplina é obrigatória");
            isValid = false;
        } else if (disciplina.length() < 3) {
            binding.disciplina.setError("Disciplina deve ter ao menos 3 caracteres");
            isValid = false;
        } else {
            binding.disciplina.setError(null);
        }

        // Data validation
        LocalDate data = null;
        String dataStr = binding.data.getText().toString().trim();
        if (!isValidData(dataStr)) {
            binding.data.setError("Data inválida. Use o formato DD/MM/YYYY");
            isValid = false;
        } else {
            // Convertendo a string de data em LocalDate
            try {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                // Agora você tem o objeto LocalDate 'data' para usar como desejar
            } catch (DateTimeParseException e) {
                // Se ocorrer um erro ao analisar a data, trate-o conforme necessário
                e.printStackTrace();
                isValid = false;
            }
        }

        // Title validation (example: optional, maximum length)
        String titulo = binding.titulo.getText().toString().trim();
        if (titulo.length() > 50) {
            binding.titulo.setError("Título excede o limite de 50 caracteres");
            isValid = false;
        } else {
            binding.titulo.setError(null);
        }

        // Description validation (example: no specific validation)
        String descricao = binding.descricao.getText().toString().trim();

        if (isValid) {
            saveTask(disciplina, data, titulo, descricao);

            clearInputsAndErrors();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveTask(String disciplina, LocalDate data, String titulo, String descricao) {
        // Implemente sua lógica para salvar a tarefa no banco de dados, armazenamento local, etc.
        // Você pode usar Room, SharedPreferences, Firebase ou outras soluções de armazenamento

        // Crie uma nova instância de Task com os dados fornecidos
        Task newTask = new Task(disciplina, descricao, titulo, data);

        // Crie um Intent para retornar os dados da nova tarefa para a TasksActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("newTask", newTask);
        setResult(RESULT_OK, resultIntent);

        // Finalize a atividade para voltar à TasksActivity
        finish();
    }

    private boolean isValidData(String data) {
        return data.matches("\\d{2}/\\d{2}/\\d{4}"); // Simple DD/MM/YYYY format check
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDataEntregaEditText();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDataEntregaEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.data.setText(dateFormat.format(calendar.getTime()));
    }

    private void clearInputsAndErrors() {
        binding.disciplina.setText("");
        binding.data.setText("");
        binding.titulo.setText("");
        binding.descricao.setText("");
    }
}