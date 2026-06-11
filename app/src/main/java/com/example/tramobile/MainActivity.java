package com.example.tramobile;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tramobile.database.AppDatabase;
import com.example.tramobile.database.CheckIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}