package com.example.schooltasks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schooltasks.databinding.ActivityCriarTurmasBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CriarTurmasActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ActivityCriarTurmasBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String nomeUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCriarTurmasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUserName();

        binding.backBtn.setOnClickListener(v -> finish());

        binding.salvarBtn.setOnClickListener(v -> {
            // TODO: 11/08/2024
            validateFields();
        });
    }

    private void getUserName() {
        DocumentReference userDocRef = db.collection("users").document(user.getUid());
        userDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Supondo que você quer buscar um campo chamado "nome"
                            nomeUser = documentSnapshot.getString("nome");
                            // Faça algo com o valor do campo "nome"
                            Log.d("Firestore", "Nome do usuário: " + nomeUser);
                        } else {
                            Log.d("Firestore", "Documento do usuário não encontrado.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Erro ao buscar o documento", e);
                    }
                });
    }

    private Turma validateFields() {
        String nome = binding.nomeInput.getText().toString().trim();

        if (!nome.isEmpty()) {
            criarTurma(new Turma(nome, user.getUid(), nomeUser));
        }

        if (nome.isEmpty()) {
            Toast.makeText(this, "Nome está vazio", Toast.LENGTH_SHORT).show();
            binding.nomeLayout.setError("Nome é obrigatório");
        }

        return null;
    }

    private void criarTurma(Turma turma) {
        db.collection("turma")
                .add(turma)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Falha", Toast.LENGTH_SHORT).show());
    }
}