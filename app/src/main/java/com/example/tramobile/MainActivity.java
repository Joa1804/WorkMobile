package com.example.tramobile;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

                // Deleta do ROOM
                db.checkInDao().deletar(item.id);

                // Deleta do Firebase
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("checkins")
                        .child(userId).child(String.valueOf(item.id)).removeValue();

                carregarCheckIns();

                Toast.makeText(MainActivity.this, "Check-in removido!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

}