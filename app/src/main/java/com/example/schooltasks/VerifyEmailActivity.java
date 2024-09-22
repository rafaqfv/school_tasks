package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.classes.HelperClass;
import com.example.schooltasks.databinding.ActivityVerificarEmailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {
    private ActivityVerificarEmailBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityVerificarEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        binding.btnBack.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        verifyEmail();

        // Configura o AuthStateListener
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                currentUser.reload().addOnSuccessListener(aVoid -> {
                    if (currentUser.isEmailVerified()) {
                        // Email foi verificado com sucesso
                        View rootView = findViewById(android.R.id.content);
                        HelperClass.showSnackbar(rootView, this, "Email verificado com sucesso!");
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                });
            }
        };
    }

    private void verifyEmail() {
        binding.btnVerificaEmail.setOnClickListener(v -> {
            binding.progressBarVerification.setVisibility(View.VISIBLE);
            user.sendEmailVerification().addOnSuccessListener(task -> {
                        binding.progressBarVerification.setVisibility(View.GONE);
                        View rootView = findViewById(android.R.id.content);
                        HelperClass.showSnackbar(rootView, this, "Email de verificação enviado.");
                    })
                    .addOnFailureListener(e -> {
                        binding.progressBarVerification.setVisibility(View.GONE);
                        View rootView = findViewById(android.R.id.content);
                        HelperClass.showSnackbar(rootView, this, "Erro ao enviar o email de verificação.");
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Adiciona o AuthStateListener para monitorar o estado de autenticação
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove o AuthStateListener ao parar a atividade
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}