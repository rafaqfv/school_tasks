package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        binding.backBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.btnSalvar.setOnClickListener(v -> {
            if (validateFields()) {
                cadastrarUsuarioAuth();
            }
        });
    }

    private void addUserDoc(FirebaseUser user) {
        String nome = binding.nomeInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nome", nome);
        userMap.put("email", email);

        db.collection("users").document(user.getUid()).set(userMap)
                .addOnSuccessListener(documentReference -> {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, "Cadastro realizado com sucesso.", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
                    snackbar.setBackgroundTint(getColor(R.color.md_theme_primaryContainer));
                    snackbar.show();
                    binding.progressBar2.setVisibility(View.GONE);
                    finish();
                    startActivity(new Intent(this, VerificarEmailActivity.class));

                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, "Erro ao cadastrar usuário.", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
                    snackbar.setBackgroundTint(getColor(R.color.md_theme_primaryContainer));
                    snackbar.show();
                    binding.progressBar2.setVisibility(View.GONE);
                });
    }

    private void cadastrarUsuarioAuth() {
        if (validateFields()) {

            String email = binding.emailInput.getText().toString().trim();
            String senha = binding.senhaInput.getText().toString().trim();
            binding.progressBar2.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            addUserDoc(user);
                        } else {
                            Exception e = task.getException();
                            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.progressBar2.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private boolean validateFields() {
        String nome = binding.nomeInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String senha = binding.senhaInput.getText().toString().trim();

        if (!nome.isEmpty() && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && senha.length() >= 6)
            return true;

        if (nome.isEmpty()) {
            binding.nome.setError("Nome vazio.");
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Email inválido.");
        }
        if (senha.length() < 6) {
            binding.senha.setError("Senha menor do que 6 dígitos.");
        }
        return false;
    }
}