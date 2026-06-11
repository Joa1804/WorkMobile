package com.example.tramobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tramobile.database.AppDatabase;
import com.example.tramobile.database.CheckIn;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainNovoCheckInActivity extends AppCompatActivity {

    private EditText etNome, etDescricao, etCategoria;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0, longitude = 0;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_novo_check_in);

        etNome = findViewById(R.id.etNome);
        etDescricao = findViewById(R.id.etDescricao);
        etCategoria = findViewById(R.id.etCategoria);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnCapturar = findViewById(R.id.btnCapturarLocalizacao);
        btnCapturar.setOnClickListener(v -> capturarLocalizacao());

        Button btnSalvar = findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(v -> salvarCheckIn());
    }

    private void capturarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(this, "📍 Localização capturada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Não foi possível obter localização.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarCheckIn() {
        String nome = etNome.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();

        if (nome.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Preencha nome e categoria!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitude == 0 && longitude == 0) {
            Toast.makeText(this, "Capture a localização primeiro!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Salva no ROOM
        CheckIn checkIn = new CheckIn();
        checkIn.nome = nome;
        checkIn.descricao = descricao;
        checkIn.categoria = categoria;
        checkIn.latitude = latitude;
        checkIn.longitude = longitude;
        checkIn.userId = userId;
        checkIn.timestamp = System.currentTimeMillis();

        AppDatabase.getInstance(this).checkInDao().inserir(checkIn);

        // Salva no Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("checkins").child(userId);
        String key = ref.push().getKey();
        ref.child(key).setValue(checkIn);

        Toast.makeText(this, "Check-in salvo!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            capturarLocalizacao();
        }
    }
}