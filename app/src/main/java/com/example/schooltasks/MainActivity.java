package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.schooltasks.adapter.TurmaAdapter;
import com.example.schooltasks.databinding.ActivityTurmasBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
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
        inicializarComponentes();
        listenForTurmaChanges();
        botoes();

    }

    private void botoes() {
        binding.btnMenu.setOnClickListener(v -> bottomSheetUserActions());

        binding.addTurma.setOnClickListener(v -> bottomSheetTurma());
    }

    private void inicializarComponentes() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listaTurmas = new ArrayList<>();
        adapter = new TurmaAdapter(listaTurmas, this);
        binding.turmasRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.turmasRecycler.setAdapter(adapter);
        getUserName();
        isEmailVerified();
    }

    private void isEmailVerified() {
        boolean isEmailVerified = mAuth.getCurrentUser().isEmailVerified();
        if (!isEmailVerified) {
            binding.turmasRecycler.setVisibility(View.GONE);
            binding.addTurma.setVisibility(View.GONE);
            View rootView = findViewById(android.R.id.content);
            SnackbarHelper.showSnackbar(rootView, this, "E-mail não verificado.");
        }
    }

    private void getUserName() {
        DocumentReference userDocRef = db.collection("users").document(mAuth.getUid());
        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nomeUser = documentSnapshot.getString("nome");
                        System.out.println("Nome do usuário: " + nomeUser);
                        return;
                    }
                    Log.w("Firestore", "Documento do usuário não encontrado");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Erro ao buscar o documento", e));
    }

    private void listenForTurmaChanges() {
        db.collection("turmaAlunos").whereEqualTo("idAluno", mAuth.getUid())
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Erro ao buscar turmas do usuário", e);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.w("Firestore", "Nenhuma turma encontrada para o usuário");
                        return;
                    }

                    ArrayList<String> idTurmas = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        String idTurma = doc.getString("idTurma");
                        if (idTurma != null) {
                            idTurmas.add(idTurma);
                        }
                    }

                    if (!idTurmas.isEmpty()) {
                        listenForTurmaDetails(idTurmas);
                    }
                });
    }

    private void listenForTurmaDetails(ArrayList<String> idTurmas) {
        listaTurmas.clear();

        db.collection("turma")
                .whereIn(FieldPath.documentId(), idTurmas)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Erro ao buscar detalhes das turmas", e);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        listaTurmas.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Turma turma = doc.toObject(Turma.class);
                            if (turma != null) {
                                turma.setId(doc.getId());
                                listaTurmas.add(turma);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firestore", "Nenhuma turma encontrada");
                    }
                });
    }


    private void criarTurma(Turma turma) {
        db.collection("turma")
                .add(turma)
                .addOnSuccessListener(documentReference -> {
                    entrarNaTurma(mAuth.getUid(), documentReference.getId());
                    View rootView = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, "Turma criada com sucesso", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
                    snackbar.setBackgroundTint(getColor(R.color.md_theme_primaryContainer));
                    snackbar.show();
                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, "Erro criar turma.", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
                    snackbar.setBackgroundTint(getColor(R.color.md_theme_primaryContainer));
                    snackbar.show();
                });
    }

    private void entrarNaTurma(String idUser, String idTurma) {
        Map<String, Object> turmaAlunos = new HashMap<>();
        turmaAlunos.put("idTurma", idTurma);
        turmaAlunos.put("idAluno", idUser);
        db.collection("turmaAlunos").add(turmaAlunos)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Aluno entrou na turma com ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Erro ao entrar no aluno na turma", e);
                });
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

    private void bottomSheetUserActions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_main_actions, null);

        MaterialButton logOut = view1.findViewById(R.id.logOut);
        MaterialButton userBtn = view1.findViewById(R.id.usuario);

        userBtn.setText(nomeUser);

        logOut.setOnClickListener(vv -> {
            bottomSheetDialog.dismiss();
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        userBtn.setOnClickListener(vvv -> {
            bottomSheetDialog.dismiss();
            View rootView = findViewById(android.R.id.content);
            SnackbarHelper.showSnackbar(rootView, this, "Ainda precisamos criar esta atividade, " + nomeUser);
        });

        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mAuth.getCurrentUser().isEmailVerified()) {
            finish();
            startActivity(new Intent(this, VerificarEmailActivity.class));
        }
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