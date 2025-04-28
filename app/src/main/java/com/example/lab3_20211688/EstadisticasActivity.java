package com.example.lab3_20211688;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_20211688.databinding.ActivityEstadisticasBinding;

public class EstadisticasActivity extends AppCompatActivity {

    private ActivityEstadisticasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEstadisticasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int correctas = getIntent().getIntExtra("correctas", 0);
        int incorrectas = getIntent().getIntExtra("incorrectas", 0);
        int sinResponder = getIntent().getIntExtra("sinResponder", 0);

        binding.txtCorrectas.setText("Correctas: " + correctas);
        binding.txtIncorrectas.setText("Incorrectas: " + incorrectas);

        // Mostrar solo si hay sin responder
        if (sinResponder > 0) {
            binding.txtSinResponder.setText("Sin responder: " + sinResponder);
            binding.txtSinResponder.setVisibility(View.VISIBLE);
        } else {
            binding.txtSinResponder.setVisibility(View.GONE);
        }

        binding.btnVolverJugar.setOnClickListener(v -> {
            Intent intent = new Intent(EstadisticasActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }
}
