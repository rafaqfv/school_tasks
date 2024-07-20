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
            binding.emailInput.setError("Email vazio");
            new Handler(getMainLooper()).postDelayed(() -> {
                binding.emailInput.setError(null);
            }, 2000);
        }
        if (senha.length() < 8) {
            binding.senhaInput.setError("Senha tem que ser maior ou igual à 8");
            new Handler(getMainLooper()).postDelayed(() -> {
                binding.senhaInput.setError(null);
            }, 2000);
        }
        return false;
    }

    public void login() {
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
                        Toast.makeText(this, "Falha ao logar", Toast.LENGTH_SHORT).show();
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
