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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        listenForAlunosChanges();
        botoes();
    }

    private void botoes() {
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

    // Método para monitorar as mudanças na coleção de 'turmaAlunos' e 'users'
    private void listenForAlunosChanges() {
        db.collection("turmaAlunos").whereEqualTo("idTurma", idTurma)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar alunos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Toast.makeText(this, "Nenhum aluno encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> idAlunos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        String idAluno = doc.getString("idAluno");
                        if (idAluno != null) {
                            idAlunos.add(idAluno);
                        }
                    }

                    if (!idAlunos.isEmpty()) {
                        listenForAlunoDetails(idAlunos);
                    }
                });
    }

    private void listenForAlunoDetails(ArrayList<String> idAlunos) {
        listaAlunos.clear();

        db.collection("users")
                .whereIn(FieldPath.documentId(), idAlunos)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar detalhes dos alunos.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        listaAlunos.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Aluno aluno = doc.toObject(Aluno.class);
                            if (aluno != null) {
                                aluno.setId(doc.getId());
                                listaAlunos.add(aluno);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Nenhum aluno encontrado.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addAluno(String idAluno) {
        bottomSheetDialog.dismiss();
        TextInputEditText emailAluno = view1.findViewById(R.id.emailInput);
        emailAluno.setText(null);
        Map<String, Object> turmaAlunos = new HashMap<>();
        turmaAlunos.put("idTurma", idTurma);
        turmaAlunos.put("idAluno", idAluno);
        db.collection("turmaAlunos").add(turmaAlunos)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Aluno entrou na turma!", Toast.LENGTH_SHORT).show();
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
                    System.out.println(queryDocumentSnapshots.size());
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
                    System.out.println("Encontrou quantos? " + queryDocumentSnapshots.size());
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

        if (!admin) return;

        Aluno aluno = listaAlunos.get(position);
        String idAluno = aluno.getId();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_update_aluno, null);
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        MaterialButton removerAluno = view1.findViewById(R.id.deleteAluno);
        MaterialButton addAdmin = view1.findViewById(R.id.addAdmin);

        addAdmin.setOnClickListener(vv -> {
            // TODO: 24/08/2024 Torná-lo admin da turma
        });
        removerAluno.setOnClickListener(vvv -> {
            bottomSheetDialog.dismiss();
            db.collection("turmaAlunos")
                    .whereEqualTo("idAluno", idAluno)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String docId = documentSnapshot.getId();

                            db.collection("turmaAlunos").document(docId).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Aluno removido da turma.", Toast.LENGTH_SHORT).show();
                                        return;
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao remover aluno da turma.", Toast.LENGTH_SHORT).show());
                        } else {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(this, "Aluno não encontrado na turma.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao buscar aluno na turma.", Toast.LENGTH_SHORT).show());
        });
    }
}