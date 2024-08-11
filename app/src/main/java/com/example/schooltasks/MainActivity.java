package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
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
            binding.progressBar2.setVisibility(View.VISIBLE);
            if (validateFields()) {
                cadastraUsuarioAuth();
            }
            binding.progressBar2.setVisibility(View.INVISIBLE);
        });
    }

    private void addUserDoc(FirebaseUser user) {
        String nome = binding.nomeInput.getText().toString().trim();
        String telefone = binding.telefoneInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nome", nome);
        userMap.put("telefone", telefone);
        userMap.put("email", email);

        db.collection("users").document(user.getUid()).set(userMap)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(this, "Usuário salvo com sucesso", Toast.LENGTH_SHORT).show();
                    binding.progressBar2.setVisibility(View.INVISIBLE);

                    new Handler(getMainLooper()).postDelayed(() -> {
                        finish();
                        startActivity(new Intent(this, LoginActivity.class));
                    }, 1500);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar usuário", Toast.LENGTH_SHORT).show();
                    binding.progressBar2.setVisibility(View.INVISIBLE);
                });
    }

    private void cadastraUsuarioAuth() {
        if (validateFields()) {

            String email = binding.emailInput.getText().toString().trim();
            String senha = binding.senhaInput.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            addUserDoc(user);
                        } else {
                            Exception e = task.getException();
                            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.progressBar2.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    private boolean validateFields() {
        String nome = binding.nomeInput.getText().toString().trim();
        String telefone = binding.telefoneInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String senha = binding.senhaInput.getText().toString().trim();

        if (!nome.isEmpty() && !telefone.isEmpty() && !email.isEmpty() && senha.length() >= 8)
            return true;

        if (nome.isEmpty()) {
            binding.nome.setError("Nome vazio.");
        }
        if (telefone.isEmpty()) {
            binding.telefone.setError("Telefone Vazio.");
        }
        if (email.isEmpty()) {
            binding.email.setError("Email vazio.");
        }
        if (senha.length() < 8) {
            binding.senha.setError("Senha menor do que 8 dígitos.");
        }
        return false;
    }
}