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
import com.example.schooltasks.databinding.ActivityResetPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.progressBar.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.recuperaContaBtn.setOnClickListener(v -> {
            buscaConta();
        });

    }

    private void buscaConta() {
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

        db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                        View rootView = findViewById(android.R.id.content);
                        HelperClass.showSnackbar(rootView, this, "Conta inexistente.");
                        binding.progressBar.setVisibility(View.GONE);
                        return;
                    }
                    sendEmail(email);
                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Erro ao buscar conta.");
                });

    }

    private void sendEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        View rootView = findViewById(android.R.id.content);
                        HelperClass.showSnackbar(rootView, this, "Email de redefinição de senha enviado.");
                        binding.emailInput.setText("");
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        View rootView = findViewById(android.R.id.content);
                        HelperClass.showSnackbar(rootView, this, "Email de redefinição de senha enviado.");
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}