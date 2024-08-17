package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityAlunosBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AlunosActivity extends AppCompatActivity {
    private ActivityAlunosBinding binding;
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAlunosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        admin = intent.getBooleanExtra("isAdmin", false);
        binding.addAluno.setVisibility(View.GONE);

        if (admin) binding.addAluno.setVisibility(View.VISIBLE);

        binding.backBtn.setOnClickListener(v -> finish());

        binding.addAluno.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);
            bottomSheetDialog.setContentView(view1);
            bottomSheetDialog.show();

            TextInputLayout emailLayout = view1.findViewById(R.id.email);
            TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
            MaterialButton btnSalvar = view1.findViewById(R.id.btnSalvar);
            TextView btnCancelar = view1.findViewById(R.id.btnCancelar);
            emailInput.requestFocus();

            btnSalvar.setOnClickListener(vv -> {
                if (emailInput.getText().toString().isEmpty()) {
                    emailLayout.setError("Digite um e-mail válido.");
                    return;
                }

                Toast.makeText(this, emailInput.getText().toString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            btnCancelar.setOnClickListener(vvv -> bottomSheetDialog.dismiss());
        });
    }
}