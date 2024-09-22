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

import com.example.schooltasks.classes.HelperClass;
import com.example.schooltasks.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
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
                    HelperClass.showSnackbar(rootView, this, "Usuário cadastrado com sucesso!");
                    binding.progressBar2.setVisibility(View.GONE);
                    finish();
                    startActivity(new Intent(this, VerifyEmailActivity.class));

                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Erro ao cadastrar usuário.");
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
                            View rootView = findViewById(android.R.id.content);
                            HelperClass.showSnackbar(rootView, this, "Erro ao cadastrar usuário.");
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
            binding.nomeInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    binding.nome.setError(null);
                }
            });
        }
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
        }
        if (senha.length() < 6) {
            binding.senha.setError("Senha menor do que 6 dígitos.");
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
}