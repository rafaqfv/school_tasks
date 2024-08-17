package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.adapter.AlunoAdapter;
import com.example.schooltasks.databinding.ActivityAlunosBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlunosActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityAlunosBinding binding;
    private boolean admin;
    private FirebaseFirestore db;
    private ArrayList<Aluno> listaAlunos;
    private String idTurma;
    private AlunoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAlunosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        idTurma = intent.getStringExtra("idTurma");
        admin = intent.getBooleanExtra("isAdmin", false);
        binding.addAluno.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();
        listaAlunos = new ArrayList<>();
        adapter = new AlunoAdapter(listaAlunos, this);
        binding.recyclerAlunos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAlunos.setAdapter(adapter);
        getAlunos();

        if (admin) binding.addAluno.setVisibility(View.VISIBLE);

        binding.backBtn.setOnClickListener(v -> finish());

        binding.addAluno.setOnClickListener(v -> {
            bottomSheetAluno();
        });
    }

    private void getAlunos() {
        listaAlunos.clear();

        db.collection("turmaAlunos").whereEqualTo("idTurma", idTurma)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar turmas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        String idAluno = doc.getString("idAluno");

                        db.collection("users")
                                .document(idAluno)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Aluno aluno = documentSnapshot.toObject(Aluno.class);
                                        if (aluno != null) {
                                            aluno.setId(documentSnapshot.getId());
                                            listaAlunos.add(aluno);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Toast.makeText(this, "Aluno não encontrado: " + idAluno, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e1 -> {
                                    Toast.makeText(this, "Erro ao buscar detalhes do aluno.", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void addAluno(String email) {
        db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    String idAluno = documentSnapshot.getId();
                    Map<String, Object> turmaAlunos = new HashMap<>();
                    turmaAlunos.put("idTurma", idTurma);
                    turmaAlunos.put("idAluno", idAluno);

                    db.collection("turmaAlunos").add(turmaAlunos)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Aluno entrou na turma!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Erro ao entrar na turma", Toast.LENGTH_SHORT).show());

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao cadastrar o aluno", Toast.LENGTH_SHORT).show());
    }

    private void bottomSheetAluno() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextInputLayout emailLayout = view1.findViewById(R.id.email);
        TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
        MaterialButton btnSalvar = view1.findViewById(R.id.btnSalvar);
        TextView btnCancelar = view1.findViewById(R.id.btnCancelar);
        emailInput.requestFocus();

        btnSalvar.setOnClickListener(vv -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                emailLayout.setError("Digite um e-mail válido.");
                return;
            }
            addAluno(email);

            Toast.makeText(this, emailInput.getText().toString(), Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        btnCancelar.setOnClickListener(vvv -> bottomSheetDialog.dismiss());
    }

    @Override
    public void onItemClick(int position) {
        Aluno aluno = listaAlunos.get(position);


    }
}