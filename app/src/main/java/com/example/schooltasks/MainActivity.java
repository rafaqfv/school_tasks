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
//import com.google.firebase.auth.FirebaseAuth;

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

        binding.btnSalvar.setOnClickListener(v -> {
            binding.progressBar2.setVisibility(View.VISIBLE);

            String nome = binding.nomeInput.getText().toString().trim();
            String telefone = binding.telefoneInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();
            String senha = binding.senhaInput.getText().toString().trim();

            if (!nome.isEmpty() && !telefone.isEmpty() && !email.isEmpty() && senha.length() >= 8) {
                mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("Nome", nome);
                                userMap.put("Telefone", telefone);
                                userMap.put("Email", email);

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
                            } else {
                                Exception e = task.getException();
                                Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                binding.progressBar2.setVisibility(View.INVISIBLE);
                            }
                        });
            }

            if (nome.isEmpty()) {
                binding.nomeInput.setError("Nome vazio.");
                new Handler(getMainLooper()).postDelayed(() -> {
                    binding.nomeInput.setError(null);
                }, 2000);
            }
            if (telefone.isEmpty()) {
                binding.telefoneInput.setError("Telefone Vazio.");
                new Handler(getMainLooper()).postDelayed(() -> {
                    binding.telefoneInput.setError(null);
                }, 2000);
            }
            if (email.isEmpty()) {
                binding.emailInput.setError("Email vazio.");
                new Handler(getMainLooper()).postDelayed(() -> {
                    binding.emailInput.setError(null);
                }, 2000);
            }
            if (senha.length() < 8) {
                binding.senhaInput.setError("Senha menor do que 8 dígitos.");
                new Handler(getMainLooper()).postDelayed(() -> {
                    binding.senhaInput.setError(null);
                }, 2000);
            }
            binding.progressBar2.setVisibility(View.INVISIBLE);
        });
    }
}