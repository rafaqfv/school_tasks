package com.example.schooltasks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltasks.classes.HelperClass;
import com.example.schooltasks.classes.Task;
import com.example.schooltasks.classes.Turma;
import com.example.schooltasks.adapter.TaskAdapter;
import com.example.schooltasks.adapter.TurmaAdapter;
import com.example.schooltasks.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;
    private ArrayList<Turma> listaTurmas;
    private String nomeUser;
    private ArrayList<String> idTurmas;
    private ArrayList<Task> nextTasks;
    private TurmaAdapter adapter;
    private TaskAdapter taskAdapter;
    private BottomSheetDialog bottomSheetDialogTasks;
    private ArrayList<String> idTasks;
    private ArrayList<Task> filteredTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarComponentes();
        botoes();
    }

    private void botoes() {
        binding.btnMenu.setOnClickListener(v -> bottomSheetUserActions());

        binding.addTurma.setOnClickListener(v -> bottomSheetTurma());

        binding.notificationBtn.setOnClickListener(v -> bottomSheetUserNotifications());
    }

    private void inicializarComponentes() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listaTurmas = new ArrayList<>();
        nextTasks = new ArrayList<>();
        idTasks = new ArrayList<>();
        idTurmas = new ArrayList<>();
        filteredTasks = new ArrayList<>();
        adapter = new TurmaAdapter(listaTurmas, this);
        binding.turmasRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.turmasRecycler.setAdapter(adapter);
        listenForTurmaChanges();
        getUserName();
    }

    private void getUserName() {
        DocumentReference userDocRef = db.collection("users").document(mAuth.getUid());
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nomeUser = documentSnapshot.getString("nome");
                return;
            }
            Log.w("Firestore", "Documento do usuário não encontrado");
        }).addOnFailureListener(e -> Log.w("Firestore", "Erro ao buscar o documento", e));
    }

    private void listenForTurmaChanges() {
        CollectionReference turmaAlunosRef = db.collection("turmaAlunos");
        Query query = turmaAlunosRef.whereEqualTo("idAluno", mAuth.getUid());
        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w("Firestore", "Erro ao buscar turmas do usuário", e);
                return;
            }

            if (value == null || value.isEmpty()) {
                Log.w("Firestore", "Nenhuma turma encontrada para o usuário");
                return;
            }

            for (QueryDocumentSnapshot doc : value) {
                String idTurma = doc.getString("idTurma");
                if (idTurma != null) idTurmas.add(idTurma);
            }

            if (!idTurmas.isEmpty()) {
                listenForTurmaDetails(idTurmas);
                getTasksFromTheClass();
            }
        });
    }

    private void listenForTurmaDetails(ArrayList<String> idTurmas) {
        listaTurmas.clear();
        CollectionReference turmaRef = db.collection("turma");
        Query query = turmaRef.whereIn(FieldPath.documentId(), idTurmas);

        query.addSnapshotListener((value, e) -> {
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
        CollectionReference turmaRef = db.collection("turma");
        turmaRef.add(turma).addOnSuccessListener(documentReference -> {
            entrarNaTurma(mAuth.getUid(), documentReference.getId());
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Turma criada!");
            Intent intent = new Intent(this, TaskActivity.class);
            intent.putExtra("idTurma", documentReference.getId());
            intent.putExtra("idAdmin", mAuth.getUid());
            intent.putExtra("nomeTurma", turma.getNome());
            finish();
            startActivity(intent);
        }).addOnFailureListener(e -> {
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Erro ao criar turma.");
        });
    }

    private void entrarNaTurma(String idUser, String idTurma) {
        Map<String, Object> turmaAlunos = new HashMap<>();
        turmaAlunos.put("idTurma", idTurma);
        turmaAlunos.put("idAluno", idUser);
        CollectionReference turmaAlunosRef = db.collection("turmaAlunos");
        turmaAlunosRef.add(turmaAlunos).addOnSuccessListener(documentReference -> {
            Log.d("Firestore", "Aluno entrou na turma com ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
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

        btnCriarTurma.setOnClickListener(v -> {
            if (nomeTurmaInput.getText().toString().isEmpty()) {
                nomeLayout.setError("Digite um nome válido.");
                nomeTurmaInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        nomeLayout.setError(null);
                    }
                });
                return;
            }
            List<String> admin = new ArrayList<>();
            admin.add(mAuth.getCurrentUser().getUid());
            criarTurma(new Turma(nomeTurmaInput.getText().toString(), admin, nomeUser));
            bottomSheetDialog.dismiss();
        });
        btnCancelar.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private void bottomSheetUserActions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_main_actions, null);

        MaterialButton logOut = view1.findViewById(R.id.logOut);
        MaterialButton userBtn = view1.findViewById(R.id.usuario);

        userBtn.setText(nomeUser);

        logOut.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        userBtn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            View rootView = findViewById(android.R.id.content);
            HelperClass.showSnackbar(rootView, this, "Ainda precisamos criar esta atividade, " + nomeUser);
        });

        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    private void bottomSheetUserNotifications() {
        bottomSheetDialogTasks = new BottomSheetDialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_notification, null);
        bottomSheetDialogTasks.setContentView(view1);

        RecyclerView rv = view1.findViewById(R.id.notificationTasks);
        rv.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(filteredTasks);
        rv.setAdapter(taskAdapter);

        if (filteredTasks.isEmpty()) {
            TextView titulo = view1.findViewById(R.id.titleTasks);
            titulo.setText("Não há tarefas próximas");
        }

        adapter.notifyDataSetChanged();

        bottomSheetDialogTasks.show();
    }

    private void getTasksFromTheClass() {
        if (idTurmas == null || idTurmas.isEmpty()) {
            Log.w("Firestore", "idTurmas está vazio. Não é possível buscar tarefas.");
            return;
        }

        CollectionReference tasksRef = db.collection("tasks");
        Query query = tasksRef.whereIn("idTurma", idTurmas);
        query.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception exception = task.getException();
                Log.w("Firestore", "Erro ao pegar as tarefas", exception);
                return;
            }

            nextTasks.clear();
            if (task.getResult().isEmpty()) {
                idTasks.clear();
                return;
            }
            for (QueryDocumentSnapshot document : task.getResult()) {
                idTasks.add(document.getId());
            }
            getTasks(idTasks);
        });
    }

    private void getTasks(ArrayList<String> idTasks) {
        CollectionReference tasksRef = db.collection("tasks");
        Query queryByIds = tasksRef.whereIn(FieldPath.documentId(), idTasks)
                .orderBy("dataDeEntrega", Query.Direction.ASCENDING);
        Date hoje = zerarHoras(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(hoje);
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        Date fiveDaysLater = calendar.getTime();

        queryByIds.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("Firestore", "Erro ao pegar as tarefas por ID!!!", task.getException());
                return;
            }

            for (QueryDocumentSnapshot document : task.getResult()) {
                Task taskItem = document.toObject(Task.class);
                taskItem.setId(document.getId());

                Date dataDeEntrega = taskItem.getDataDeEntrega().toDate();
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dataDeEntrega);
                calendar1.add(Calendar.HOUR_OF_DAY, 3);
                dataDeEntrega = calendar1.getTime();
                if (dataDeEntrega != null
                        && dataDeEntrega.compareTo(hoje) >= 0
                        && dataDeEntrega.compareTo(fiveDaysLater) <= 0)
                    filteredTasks.add(taskItem);
            }

            if (!filteredTasks.isEmpty()) binding.containerEllipse.setVisibility(View.VISIBLE);
        });
    }

    private Date zerarHoras(Date dataParaSerZerada) {
        Calendar zerarCalendar = Calendar.getInstance();
        zerarCalendar.setTime(dataParaSerZerada);
        zerarCalendar.set(Calendar.HOUR_OF_DAY, 0);
        zerarCalendar.set(Calendar.MINUTE, 0);
        zerarCalendar.set(Calendar.SECOND, 0);
        zerarCalendar.set(Calendar.MILLISECOND, 0);
        dataParaSerZerada = zerarCalendar.getTime();

        return dataParaSerZerada;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mAuth.getCurrentUser().isEmailVerified()) {
            finish();
            startActivity(new Intent(this, VerifyEmailActivity.class));
        }
    }

    @Override
    public void onItemClick(int position) {
        Turma turma = listaTurmas.get(position);
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("idTurma", turma.getId());
        intent.putExtra("nomeTurma", turma.getNome());

        finish();
        startActivity(intent);
    }
}