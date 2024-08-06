package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        binding.btnCadastrar.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (validateFields()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                login();
            }
            binding.progressBar.setVisibility(View.INVISIBLE);
        });
    }

    public boolean validateFields() {
        String email = binding.emailInput.getText().toString().trim();
        String senha = binding.senhaInput.getText().toString().trim();

        if (!email.isEmpty() && !senha.isEmpty() && senha.length() >= 8) {
            return true;
        }

        if (email.isEmpty()) {
            binding.email.setError("Email vazio");
        }
        if (senha.length() < 8) {
            binding.senha.setError("Senha inválida: menor que 8 caracteres");
        }
        return false;
    }

    private void login() {
        String email = binding.emailInput.getText().toString().trim();
        String senha = binding.senhaInput.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(this, TasksActivity.class));
                    } else {
                        Exception e = task.getException();
                        Toast.makeText(this, "Erro ao logar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, TasksActivity.class));
        }
    }
}