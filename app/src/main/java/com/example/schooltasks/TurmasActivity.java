package com.example.schooltasks;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schooltasks.adapter.Turma;
import com.example.schooltasks.adapter.TurmaAdapter;
import com.example.schooltasks.databinding.ActivityTurmasBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TurmasActivity extends AppCompatActivity implements OnItemClickListener {
    private FirebaseFirestore db;
    private ActivityTurmasBinding binding;
    private TurmaAdapter adapter;
    private ArrayList<Turma> listaTurmas = new ArrayList<>();
    private OnItemClickListener listener;

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
        binding.turmasRecycler.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        adapter = new TurmaAdapter(listaTurmas, listener);
        binding.turmasRecycler.setAdapter(adapter);

        binding.addTurma.setOnClickListener(v -> {
            startActivity(new Intent(this, CriarTurmasActivity.class));
        });

    }

    private void getTurmas() {

        db.collection("turma")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao buscar turmas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot dc : value) {
                        Turma turma = dc.toObject(Turma.class);
                        turma.setId(dc.getId());
                        listaTurmas.add(turma);
                    }
                });

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Turma turma = listaTurmas.get(position);

        // Todo

    }
}