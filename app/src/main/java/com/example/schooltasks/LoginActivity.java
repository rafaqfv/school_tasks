package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityLoginBinding;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
//    private FirebaseAuth mAuth;

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
//        mAuth = FirebaseAuth.getInstance();

        binding.btnCadastrar.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

//        binding.btnLogin.setOnClickListener(v -> {
//
//            binding.progressBar.setVisibility(View.VISIBLE);
//
//            String email = binding.emailInput.getText().toString();
//            String senha = binding.senhaInput.getText().toString();
//
//            if (!email.isEmpty() && !senha.isEmpty() && senha.length() >= 8) {
//                mAuth.signInWithEmailAndPassword(email, senha)
//                        .addOnCompleteListener(this, task -> {
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show();
//                                binding.progressBar.setVisibility(View.INVISIBLE);
//                                startActivity(new Intent(this, TasksActivity.class));
//                            } else {
//                                Toast.makeText(this, "Erro ao logar", Toast.LENGTH_SHORT).show();
//                                binding.progressBar.setVisibility(View.INVISIBLE);
//                            }
//                        });
//            }
//
//            if (email.isEmpty()) {
//                binding.email.setError("Vazio");
//            }
//            if (senha.length() < 8) {
//                binding.senha.setError("Têm que ser maior ou igual à 8");
//            }
//        });
//        binding.progressBar.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(this, TasksActivity.class));
//        }
//    }
    }
}