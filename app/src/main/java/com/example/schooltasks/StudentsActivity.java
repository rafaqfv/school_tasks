package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.adapter.AlunoAdapter;
import com.example.schooltasks.classes.Aluno;
import com.example.schooltasks.classes.HelperClass;
import com.example.schooltasks.databinding.ActivityStudentsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentsActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityStudentsBinding binding;
    private boolean admin;
    private FirebaseFirestore db;
    private ArrayList<Aluno> listaAlunos;
    private String idTurma;
    private AlunoAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;
    private View view1;
    private FirebaseAuth mAuth;
    private String idAdmin;
    private CollectionReference turmaAlunosRef;
    private ArrayList<String> admins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarComponentes();
        getAdmins();
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
        idAdmin = intent.getStringExtra("idAdmin");
        binding.addAluno.setVisibility(View.GONE);
        if (admin) binding.addAluno.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        turmaAlunosRef = db.collection("turmaAlunos");
        listaAlunos = new ArrayList<>();
        admins = new ArrayList<>();
        adapter = new AlunoAdapter(listaAlunos, this, admins);
        binding.recyclerAlunos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAlunos.setAdapter(adapter);
        bottomSheetDialog = new BottomSheetDialog(this);
        view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);
    }

    private void getAdmins() {
        DocumentReference turmaRef = db.collection("turma").document(idTurma);
        turmaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.w("Firestore", "Turma não encontrada");
                return;
            }

            Object adminObject = documentSnapshot.get("admin");
            if (adminObject instanceof String) admins.add((String) adminObject);
            if (adminObject instanceof List) admins.addAll((List<String>) adminObject);

            listenForAlunosChanges();
        }).addOnFailureListener(e -> Log.w("Firestore", "Erro ao pegar documento: " + e));
    }

    private void listenForAlunosChanges() {
        Query query = turmaAlunosRef.whereEqualTo("idTurma", idTurma);
        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w("Firestore", "Erro ao buscar alunos da turma", e);
                return;
            }

            if (value == null || value.isEmpty()) {
                Log.w("Firestore", "Nenhum aluno encontrado para a turma");
                return;
            }

            ArrayList<String> idAlunos = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                String idAluno = doc.getString("idAluno");
                if (idAluno != null) idAlunos.add(idAluno);
            }

            if (!idAlunos.isEmpty()) listenForAlunoDetails(idAlunos);
        });
    }

    private void listenForAlunoDetails(ArrayList<String> idAlunos) {
        ArrayList<Aluno> alunosAdmins = new ArrayList<>();
        ArrayList<Aluno> naoAdmins = new ArrayList<>();
        listaAlunos.clear();
        CollectionReference alunosRef = db.collection("users");
        Query query = alunosRef.whereIn(FieldPath.documentId(), idAlunos);

        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w("Firestore", "Erro ao buscar detalhes dos alunos", e);
                return;
            }

            if (value == null && value.isEmpty()) {
                Log.w("Firestore", "Nenhum aluno encontrado");
                return;
            }

            listaAlunos.clear();
            for (QueryDocumentSnapshot doc : value) {
                Aluno aluno = doc.toObject(Aluno.class);
                if (aluno == null) return;
                aluno.setId(doc.getId());
                if (admins.contains(aluno.getId())) {
                    alunosAdmins.add(aluno);
                    continue;
                }
                naoAdmins.add(aluno);
            }

            listaAlunos.addAll(alunosAdmins);
            listaAlunos.addAll(naoAdmins);


            adapter.notifyDataSetChanged();
        });
    }

    private void addAluno(String idAluno) {
        bottomSheetDialog.dismiss();

        TextInputEditText emailAluno = view1.findViewById(R.id.emailInput);
        emailAluno.setText(null);

        Map<String, Object> turmaAlunos = new HashMap<>();
        turmaAlunos.put("idTurma", idTurma);
        turmaAlunos.put("idAluno", idAluno);

        turmaAlunosRef.add(turmaAlunos).addOnSuccessListener(documentReference -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Aluno adicionado à turma!");
        }).addOnFailureListener(e -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Erro ao adicionar aluno à turma.");
        });
    }

    private void bottomSheetAluno() {
        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();

        TextInputLayout emailLayout = view1.findViewById(R.id.email);
        TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
        MaterialButton btnSalvar = view1.findViewById(R.id.btnSalvar);
        TextView btnCancelar = view1.findViewById(R.id.btnCancelar);
        emailInput.requestFocus();

        btnSalvar.setOnClickListener(v -> {
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
        btnCancelar.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private void buscaAluno(String emailAluno) {
        TextInputLayout emailLayout = view1.findViewById(R.id.email);
        TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", emailAluno);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                emailLayout.setError("Usuário inexistente.");
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
        }).addOnFailureListener(e -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Erro ao buscar aluno.");
        });
    }

    private void validaAlunoNaTurma(String idAluno) {
        TextInputLayout emailLayout = view1.findViewById(R.id.email);
        TextInputEditText emailInput = view1.findViewById(R.id.emailInput);
        Query query = turmaAlunosRef.whereEqualTo("idAluno", idAluno).whereEqualTo("idTurma", idTurma);

        // Verificar se o aluno já está na turma
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        }).addOnFailureListener(e -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Erro ao adicionar aluno.");
        });
    }

    private void removerAluno(String idAluno) {
        Query query = turmaAlunosRef.whereEqualTo("idAluno", idAluno).whereEqualTo("idTurma", idTurma);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                View rootView = findViewById(android.R.id.content);
                HelperClass.showSnackbar(rootView, this, "Aluno não encontrado na turma");
                return;
            }

            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
            String docId = documentSnapshot.getId();
            DocumentReference documentReference = turmaAlunosRef.document(docId);

            documentReference.delete().addOnSuccessListener(aVoid -> {
                View rootView = findViewById(android.R.id.content);
                HelperClass.showSnackbar(rootView, this, "Aluno removido da turma!");
            }).addOnFailureListener(e -> {
                View rootView = findViewById(android.R.id.content);
                HelperClass.showSnackbar(rootView, this, "Erro ao remover o aluno da turma");
            });
        }).addOnFailureListener(e -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Erro ao remover o aluno da turma");
        });
    }

    private void addAdmin(String idAluno) {
        bottomSheetDialog.dismiss();
        DocumentReference turmaRef = db.collection("turma").document(idTurma);

        if (admins.contains(idAluno)) {
            bottomSheetDialog.dismiss();
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Aluno já é administrador");
            return;
        }

        admins.add(idAluno);
        turmaRef.update("admin", admins).addOnSuccessListener(aVoid -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Aluno virou administrador");

        }).addOnFailureListener(e -> Log.w("Firestore", "Erro ao torná-lo administrador"));
    }

    private void removeAdmin(String idAluno) {
        bottomSheetDialog.dismiss();
        DocumentReference turmaRef = db.collection("turma").document(idTurma);

        if (!admins.contains(idAluno)) {
            bottomSheetDialog.dismiss();
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Aluno não é administrador");
            return;
        }

        admins.remove(idAluno);
        turmaRef.update("admin", admins).addOnSuccessListener(aVoid -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Aluno não é mais administrador");
        }).addOnFailureListener(e -> Log.w("Firestore", "Erro ao torná-lo administrador"));
    }

    @Override
    public void onItemClick(int position) {
        mAuth = FirebaseAuth.getInstance();

        if (!admin) return;

        Aluno aluno = listaAlunos.get(position);
        String idAluno = aluno.getId();

        if (idAluno.equals(mAuth.getCurrentUser().getUid())) return;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_update_aluno, null);
        bottomSheetDialog.setContentView(view1);

        MaterialButton removerAluno = view1.findViewById(R.id.deleteAluno);
        MaterialButton addAdmin = view1.findViewById(R.id.addAdmin);
        MaterialButton removeAdmin = view1.findViewById(R.id.removeAdmin);
        if (admins.contains(idAluno)) {
            addAdmin.setVisibility(View.GONE);
            removeAdmin.setVisibility(View.VISIBLE);
        }

        removerAluno.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            removerAluno(idAluno);
        });
        addAdmin.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            addAdmin(idAluno);
        });
        removeAdmin.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            removeAdmin(idAluno);
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}