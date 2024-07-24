package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityUpdateTaskBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        binding.backBtn.setOnClickListener(v -> finish());
        datePicker();

        Intent intent = getIntent();
        String disciplina = intent.getStringExtra("disciplina");
        String titulo = intent.getStringExtra("titulo");
        String dataDeEntrega = intent.getStringExtra("data");
        String descricao = intent.getStringExtra("descricao");
        String id = intent.getStringExtra("id");

        binding.data.setText(dataDeEntrega);
        binding.disciplina.setText(disciplina);
        binding.titulo.setText(titulo);
        binding.descricao.setText(descricao);


    }

    public void updateTask() {



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
}