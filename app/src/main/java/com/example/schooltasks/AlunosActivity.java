package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private BottomSheetDialog bottomSheetDialog;
    private View view1;

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
        inicializarComponentes();
        getAlunos();
        cliques();
    }

    private void cliques() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.addAluno.setOnClickListener(v -> bottomSheetAluno());
    }

    private void inicializarComponentes() {
        Intent intent = getIntent();
        idTurma = intent.getStringExtra("idTurma");
        admin = intent.getBooleanExtra("isAdmin", false);
        binding.addAluno.setVisibility(View.GONE);
        if (admin) binding.addAluno.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        listaAlunos = new ArrayList<>();
        adapter = new AlunoAdapter(listaAlunos, this);
        binding.recyclerAlunos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAlunos.setAdapter(adapter);
        bottomSheetDialog = new BottomSheetDialog(this);
        view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);
    }

    // TODO: 24/08/2024 Refatorar método de pegar alunos da turma. 
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

    private void addAluno(String idAluno) {
        Map<String, Object> turmaAlunos = new HashMap<>();
        turmaAlunos.put("idTurma", idTurma);
        turmaAlunos.put("idAluno", idAluno);
        db.collection("turmaAlunos").add(turmaAlunos)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Aluno entrou na turma!", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao entrar na turma", Toast.LENGTH_SHORT).show());
    }

    private void bottomSheetAluno() {
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
                emailInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        emailLayout.setError(null);
                    }
                });
                return;
            }
            buscaAluno(email);
        });
        btnCancelar.setOnClickListener(vvv -> bottomSheetDialog.dismiss());
    }

    private void buscaAluno(String emailAluno) {
        TextInputLayout emailLayout = view1.findViewById(R.id.email);
        TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
        // Buscar para ver se existe o aluno
        db.collection("users").whereEqualTo("email", emailAluno)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        emailLayout.setError("Usuário não encontrado.");
                        emailInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                emailLayout.setError(null);
                            }
                        });
                        return;
                    }
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    String idAluno = documentSnapshot.getId();
                    validaAlunoNaTurma(idAluno);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao cadastrar o aluno", Toast.LENGTH_SHORT).show());
    }

    private void validaAlunoNaTurma(String idAluno) {
        TextInputLayout emailLayout = view1.findViewById(R.id.email);
        TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
        // Verificar se o aluno já está na turma
        db.collection("turmaAlunos").whereEqualTo("idAluno", idAluno)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        emailLayout.setError("Aluno já faz parte da turma");
                        emailInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                emailLayout.setError(null);
                            }
                        });
                        return;
                    }
                    addAluno(idAluno);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Aluno já está na turma", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(int position) {
        Aluno aluno = listaAlunos.get(position);
    }
}