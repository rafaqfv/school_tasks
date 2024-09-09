package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityAddTasksBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AddTasksActivity extends AppCompatActivity {
    private ActivityAddTasksBinding binding;
    private FirebaseFirestore db;
    private String idTurma;
    private Intent intent;

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
        inicializarComponents();
        binding.salvarButton.setOnClickListener(v -> validateFields());
    }

    private void inicializarComponents() {
        db = FirebaseFirestore.getInstance();
        binding.backBtn.setOnClickListener(v -> finish());
        intent = getIntent();
        idTurma = intent.getStringExtra("idTurma");

        datePicker();
    }

    private void validateFields() {
        String disciplina = binding.disciplina.getText().toString().trim();
        String dataStr = binding.data.getText().toString().trim();
        String titulo = binding.titulo.getText().toString().trim();
        String descricao = binding.descricao.getText().toString().trim();

        if (
                !titulo.isEmpty()
                        && !dataStr.isEmpty()
                        && !disciplina.isEmpty()
                        && !descricao.isEmpty()
        ) {
            saveTask(disciplina, dataStr, titulo, descricao);
        }

        if (descricao.isEmpty()) {
            binding.descricaoLayout.setError("Descrição é obrigatório.");
            binding.descricao.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.descricaoLayout.setError(null);
                }
            });
        }
        if (titulo.isEmpty()) {
            binding.tituloLayout.setError("Título é obrigatório.");
            binding.titulo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.tituloLayout.setError(null);
                }
            });
        }
        if (disciplina.isEmpty()) {
            binding.disciplinaLayout.setError("Disciplina é obrigatória.");
            binding.disciplina.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.disciplinaLayout.setError(null);
                }
            });
        }
        if (dataStr.isEmpty()) {
            binding.dataLayout.setError("Data é obrigatória.");
            binding.data.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.dataLayout.setError(null);
                }
            });
        }
    }

    private void saveTask(String disciplina, String dataStr, String titulo, String descricao) {
        Task newTask = new Task(disciplina, descricao, titulo, dataStr, idTurma);
        addDocumentDB(newTask);
        clearInputsAndErrors();
    }

    private void datePicker() {
        binding.data.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione uma data")
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointForward.now())
                            .build())
                    .build();
            datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formattedDate = formatter.format(new Date(selection));
                binding.data.setText(formattedDate);
            });
        });
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
                            View rootView = findViewById(android.R.id.content);
                            HelperClass.showSnackbar(rootView, this, "Tarefa salva com sucesso!");
                            finish();
                        }
                )
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Erro ao salvar tarefa.");
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}