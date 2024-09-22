package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.classes.HelperClass;
import com.example.schooltasks.classes.Task;
import com.example.schooltasks.databinding.ActivityTasksBinding;
import com.example.schooltasks.adapter.TaskAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements OnItemClickListener {
    private ActivityTasksBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Task> taskList;
    private TaskAdapter adapter;
    private String idTurma;
    private String idAdmin;
    private String nomeTurma;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarComponentes();
        listenForTaskChanges();
        botoes();
    }

    private void inicializarComponentes() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, this);
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerTasks.setAdapter(adapter);

        Intent intent = getIntent();
        idTurma = intent.getStringExtra("idTurma");
        idAdmin = intent.getStringExtra("idAdmin");
        nomeTurma = intent.getStringExtra("nomeTurma");
        binding.titleActivity.setText(nomeTurma.toString());

        binding.intentAddTask.setVisibility(View.GONE);

        if (mAuth.getUid().equals(idAdmin)) {
            binding.intentAddTask.setVisibility(View.VISIBLE);
            isAdmin = true;
        } else {
            isAdmin = false;
        }
    }

    private void botoes() {
        binding.btnMenu.setOnClickListener(v -> bottomSheetTurmaActions());

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.intentAddTask.setOnClickListener(v -> {
            Intent intentAddTask = new Intent(this, CreateTaskActivity.class);
            intentAddTask.putExtra("idTurma", idTurma);
            startActivity(intentAddTask);
        });
    }

    private void listenForTaskChanges() {
        db.collection("tasks")
                .whereEqualTo("idTurma", idTurma)
                .orderBy("dataDeEntrega", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Erro ao buscar tarefas", e);
                        return;
                    }

                    taskList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Task task = doc.toObject(Task.class);
                        task.setId(doc.getId());
                        taskList.add(task);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void bottomSheetTurmaActions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_turma_actions, null);

        MaterialButton verAlunos = view1.findViewById(R.id.verAlunos);
        MaterialButton deleteTurma = view1.findViewById(R.id.deleteTurma);
        MaterialButton logOutTurmaBtn = view1.findViewById(R.id.logOutTurma);
        deleteTurma.setVisibility(View.GONE);
        view1.findViewById(R.id.div2).setVisibility(View.GONE);

        if (isAdmin) {
            deleteTurma.setVisibility(View.VISIBLE);
            view1.findViewById(R.id.div).setVisibility(View.VISIBLE);
            view1.findViewById(R.id.div2).setVisibility(View.VISIBLE);
        }

        verAlunos.setOnClickListener(vv -> {
            bottomSheetDialog.dismiss();
            Intent intentAlunos = new Intent(this, StudentsActivity.class);
            intentAlunos.putExtra("idTurma", idTurma);
            intentAlunos.putExtra("isAdmin", isAdmin);
            intentAlunos.putExtra("idAdmin", idAdmin);
            startActivity(intentAlunos);
        });

        logOutTurmaBtn.setOnClickListener(vvvv -> {
            bottomSheetDialog.dismiss();
            new MaterialAlertDialogBuilder(vvvv.getContext())
                    .setTitle("Confirmação de saída")
                    .setMessage("Tem certeza que deseja sair da turma?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        logOutTurma();
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        deleteTurma.setOnClickListener(vvv -> {
            bottomSheetDialog.dismiss();
            new MaterialAlertDialogBuilder(vvv.getContext())
                    .setTitle("Confirmação de Exclusão")
                    .setMessage("Tem certeza que deseja excluir esta turma?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        deletarRelacoes();
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    private void deletarRelacoes() {
        db.collection("turmaAlunos")
                .whereEqualTo("idTurma", idTurma)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        db.collection("turmaAlunos").document(document.getId()).delete();
                    }
                    deletarTurma();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Erro ao buscar alunos da turma", e);
                });
    }

    private void logOutTurma() {

        db.collection("turmaAlunos").whereEqualTo("idAluno", mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w("Firestore", "Nenhum aluno encontrado para a turma");
                        return;
                    }
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    db.collection("turmaAlunos").document(documentSnapshot.getId()).delete()
                            .addOnSuccessListener(v -> {
                                View rootView = findViewById(android.R.id.content);
                                Toast.makeText(this, "Você saiu da turma.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(this, MainActivity.class));
                            })
                            .addOnFailureListener(v -> {
                                View rootView = findViewById(android.R.id.content);
                                HelperClass.showSnackbar(rootView, this, "Erro ao sair da turma.");
                            });
                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Erro ao sair da turma.");
                });

    }

    private void deletarTurma() {
        db.collection("turma").document(idTurma).delete()
                .addOnSuccessListener(aVoid -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Turma excluída com sucesso!");
                    finish();
                    startActivity(new Intent(this, MainActivity.class));
                })
                .addOnFailureListener(e -> {
                    View rootView = findViewById(android.R.id.content);
                    HelperClass.showSnackbar(rootView, this, "Falha ao excluir turma.");
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenForTaskChanges();
    }

    @Override
    public void onItemClick(int position) {

        if (!isAdmin) return;

        Task task = taskList.get(position);
        String disciplina, titulo, data, descricao, id;
        disciplina = task.getDisciplina();
        titulo = task.getTitulo();
        data = Task.formatarData(task.getDataDeEntrega());
        descricao = task.getDescricao();
        id = task.getId();

        Intent intent = new Intent(this, UpdateTaskActivity.class);
        intent.putExtra("disciplina", disciplina);
        intent.putExtra("titulo", titulo);
        intent.putExtra("data", data);
        intent.putExtra("descricao", descricao);
        intent.putExtra("id", id);
        intent.putExtra("idTurma", idTurma);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}