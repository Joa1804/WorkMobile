package com.example.tramobile;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tramobile.database.AppDatabase;
import com.example.tramobile.database.CheckIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView recyclerView;
    private CheckInAdapter adapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carregarCheckIns();
        configurarSwipeDelete();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = user.getUid();
        db = AppDatabase.getInstance(this);

        TextView tvNome = findViewById(R.id.tvNomeUsuario);
        tvNome.setText("Olá, " + user.getDisplayName());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnNovo = findViewById(R.id.btnNovoCheckIn);
        btnNovo.setOnClickListener(v ->
                startActivity(new Intent(this, MainNovoCheckInActivity.class))
        );

        Button btnSair = findViewById(R.id.btnSair);
        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        carregarCheckIns();

    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarCheckIns();
    }

    private void carregarCheckIns() {
        List<CheckIn> lista = db.checkInDao().listarPorUsuario(userId);
        adapter = new CheckInAdapter(lista);
        recyclerView.setAdapter(adapter);

        TextView tvContador = findViewById(R.id.tvContador);
        tvContador.setText(lista.size() + " lugar(es) registrado(s)");
    }

    private void configurarSwipeDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CheckIn item = adapter.getLista().get(position);

                db.checkInDao().deletar(item.id);

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("checkins")
                        .child(userId).child(String.valueOf(item.id)).removeValue();

                carregarCheckIns();

                Toast.makeText(MainActivity.this, "Check-in removido!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void configurarBusca() {
        TextInputEditText etBusca = findViewById(R.id.etBusca);
        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarCheckIns(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filtrarCheckIns(String query) {
        List<CheckIn> listaCompleta = db.checkInDao().listarPorUsuario(userId);
        List<CheckIn> listaFiltrada = new ArrayList<>();

        for (CheckIn item : listaCompleta) {
            if (item.nome.toLowerCase().contains(query.toLowerCase()) ||
                    item.categoria.toLowerCase().contains(query.toLowerCase())) {
                listaFiltrada.add(item);
            }
        }

        adapter = new CheckInAdapter(listaFiltrada);
        recyclerView.setAdapter(adapter);

        TextView tvContador = findViewById(R.id.tvContador);
        tvContador.setText(listaFiltrada.size() + " lugar(es) encontrado(s)");
    }

}