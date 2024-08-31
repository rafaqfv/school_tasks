package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
        botoes();
    }

    private void botoes() {
        binding.btnCadastrar.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));

        binding.btnLogin.setOnClickListener(v -> {
            if (validateFields()) login();
        });
    }

    private boolean validateFields() {
        String email = binding.emailInput.getText().toString().trim();
        String senha = binding.senhaInput.getText().toString().trim();

        if (!email.isEmpty() && !senha.isEmpty() && senha.length() >= 8) {
            return true;
        }

        if (email.isEmpty()) {
            binding.email.setError("Email vazio");
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
        }
        if (senha.length() < 8) {
            binding.senha.setError("Senha inválida: menor que 8 caracteres");
            binding.senhaInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.senha.setError(null);
                }
            });
        }
        return false;
    }

    private void login() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String email = binding.emailInput.getText().toString().trim();
        String senha = binding.senhaInput.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        Exception e = task.getException();
                        Toast.makeText(this, "Erro ao logar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}