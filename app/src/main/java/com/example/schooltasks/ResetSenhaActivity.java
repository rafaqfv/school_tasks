package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityResetSenhaBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ResetSenhaActivity extends AppCompatActivity {
    private ActivityResetSenhaBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResetSenhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.progressBar.setVisibility(View.GONE);

        binding.backBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.recuperaContaBtn.setOnClickListener(v -> {
            recuperarConta();
        });

    }

    private void recuperarConta() {
        mAuth = FirebaseAuth.getInstance();
        String email = binding.emailInput.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Email inválido.");
            binding.emailInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.email.setError(null);
                }
            });
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        View rootView = findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar.make(rootView, "Email de redefinição de senha enviado.", Snackbar.LENGTH_LONG);
                        snackbar.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
                        snackbar.setBackgroundTint(getColor(R.color.md_theme_primaryContainer));
                        snackbar.show();
                        binding.emailInput.setText("");
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        View rootView = findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar.make(rootView, "Falha ao enviar email de redefinição de senha.", Snackbar.LENGTH_LONG);
                        snackbar.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
                        snackbar.setBackgroundTint(getColor(R.color.md_theme_primaryContainer));
                        snackbar.show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}