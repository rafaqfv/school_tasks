package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.adapter.TurmaAdapter;
import com.example.schooltasks.databinding.ActivityTurmasBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TurmasActivity extends AppCompatActivity implements OnItemClickListener {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ActivityTurmasBinding binding;
    private TurmaAdapter adapter;
    private ArrayList<Turma> listaTurmas;
    private String nomeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTurmasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listaTurmas = new ArrayList<>();
        adapter = new TurmaAdapter(listaTurmas, this);
        binding.turmasRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.turmasRecycler.setAdapter(adapter);
        getTurmas();
        getUserName();

        binding.btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.addTurma.setOnClickListener(v -> {
            bottomSheetTurma();
        });
    }

    private void getUserName() {
        DocumentReference userDocRef = db.collection("users").document(mAuth.getUid());

        userDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            nomeUser = documentSnapshot.getString("nome");
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

    private void getTurmas() {
        listaTurmas.clear();

        db.collection("turmaAlunos").whereEqualTo("idAluno", mAuth.getUid())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar turmas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        String idTurma = doc.getString("idTurma");

                        db.collection("turma")
                                .document(idTurma)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        Turma turma = documentSnapshot.toObject(Turma.class);
                                        if (turma != null) {
                                            turma.setId(documentSnapshot.getId());
                                            listaTurmas.add(turma);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Toast.makeText(this, "Turma não encontrada: " + idTurma, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e1 -> {
                                    Toast.makeText(this, "Erro ao buscar detalhes da turma.", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void criarTurma(Turma turma) {
        db.collection("turma")
                .add(turma)
                .addOnSuccessListener(documentReference -> {
                    entrarNaTurma(mAuth.getUid(), documentReference.getId());
                    Toast.makeText(this, "Sucesso ao criar a turma: " + turma.getNome(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Falha ao criar a turma", Toast.LENGTH_SHORT).show());
    }

    private void entrarNaTurma(String idUser, String idTurma) {
        Map<String, Object> turmaAlunos = new HashMap<>();
        turmaAlunos.put("idTurma", idTurma);
        turmaAlunos.put("idAluno", idUser);
        db.collection("turmaAlunos").add(turmaAlunos)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Aluno entrou na turma!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao entrar na turma.", Toast.LENGTH_SHORT).show());
    }

    private void bottomSheetTurma() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_turmas, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextInputLayout nomeLayout = view1.findViewById(R.id.nomeTurma);
        TextInputEditText nomeTurmaInput = view1.findViewById(R.id.nomeTurmaInput);
        MaterialButton btnCriarTurma = view1.findViewById(R.id.btnCriarTurma);
        TextView btnCancelar = view1.findViewById(R.id.cancelarBottomSheet);
        nomeTurmaInput.requestFocus();

        btnCriarTurma.setOnClickListener(vv -> {
            if (nomeTurmaInput.getText().toString().isEmpty()) {
                nomeLayout.setError("Digite um nome válido.");
                return;
            }
            criarTurma(new Turma(nomeTurmaInput.getText().toString(), mAuth.getUid(), nomeUser));
            bottomSheetDialog.dismiss();
        });
        btnCancelar.setOnClickListener(vvv -> bottomSheetDialog.dismiss());
    }

    @Override
    public void onItemClick(int position) {
        Turma turma = listaTurmas.get(position);
        Intent intent = new Intent(this, TasksActivity.class);
        intent.putExtra("idTurma", turma.getId());
        intent.putExtra("idAdmin", turma.getAdmin());
        intent.putExtra("nomeTurma", turma.getNome());

        finish();
        startActivity(intent);
    }
}