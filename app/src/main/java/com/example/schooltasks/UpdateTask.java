package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityUpdateTaskBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.validation.Validator;

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
        botoes();
        updateFields();
        datePicker();
    }

    private void botoes() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.salvarButton.setOnClickListener(v -> updateTask(id));
        binding.excluirBtn.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("Confirmação de Exclusão")
                    .setMessage("Tem certeza que deseja excluir esta tarefa?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        deleteTask(id);
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void deleteTask(String id) {
        db.collection("tasks").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Tarefa excluída com sucesso!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Erro ao excluir tarefa.");
                });
    }

    private void updateTask(String id) {
        // Obtenha os dados atualizados das views
        String updatedDisciplina = binding.disciplina.getText().toString();
        String updatedTitulo = binding.titulo.getText().toString();
        String updatedDataDeEntrega = binding.data.getText().toString();
        String updatedDescricao = binding.descricao.getText().toString();

        // TODO: 24/08/2024 Atualizar os métodos de validação do UpdateTask.java 
        if (updatedDisciplina.isEmpty() || updatedTitulo.isEmpty() || updatedDataDeEntrega.isEmpty() || updatedDescricao.isEmpty()) {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Por favor, preencha todos os campos.");
            return;
        }

        Map<String, Object> task = new HashMap<>();
        task.put("disciplina", updatedDisciplina);
        task.put("titulo", updatedTitulo);
        task.put("dataDeEntrega", Task.converterParaTimestamp(updatedDataDeEntrega));
        task.put("descricao", updatedDescricao);

        db.collection("tasks").document(id)
                .update(task)
                .addOnSuccessListener(aVoid -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Tarefa atualizada com sucesso!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Erro ao atualizar tarefa.");
                });
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
                Date date = new Date(selection);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formattedDate = formatter.format(date);
                binding.data.setText(formattedDate);
            });
        });
    }

    private void updateFields() {
        intent = getIntent();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}