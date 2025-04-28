package com.example.lab3_20211688;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.example.lab3_20211688.Bean.PreguntaBean;
import com.example.lab3_20211688.Bean.TriviaResponse;
import com.example.lab3_20211688.Interface.TriviaService;
import com.example.lab3_20211688.databinding.ActivityTriviaBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TriviaActivity extends AppCompatActivity {

    private ActivityTriviaBinding binding;  // ViewBinding

    private List<PreguntaBean> listaPreguntas;
    private int preguntaActual = 0;

    private int tiempoTotalSegundos;
    private Thread contadorHilo;
    private boolean contadorActivo = true;

    private int cantidadPreguntas;
    private int categoriaId;
    private String dificultad;

    private int correctas = 0;
    private int incorrectas = 0;
    private int sinResponder = 0;

    private boolean tiempoAgotado = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cantidadPreguntas = getIntent().getIntExtra("cantidad", 3);
        categoriaId = getIntent().getIntExtra("categoria", 9);
        dificultad = getIntent().getStringExtra("dificultad");

        cargarPreguntas(cantidadPreguntas, categoriaId, dificultad);
        binding.btnSiguiente.setOnClickListener(v -> siguientePregunta());

    }


    private void iniciarTrivia() {
        mostrarPreguntaActual();
        iniciarContador();
    }
    private void mostrarPreguntaActual() {
        if (preguntaActual < listaPreguntas.size()) {
            PreguntaBean pregunta = listaPreguntas.get(preguntaActual);

            binding.txtTituloCategoria.setText(getNombreCategoria(categoriaId)); // Opcional
            binding.txtPreguntaNumero.setText("Pregunta " + (preguntaActual + 1) + "/" + cantidadPreguntas);
            binding.txtPregunta.setText(HtmlCompat.fromHtml(pregunta.getQuestion(), HtmlCompat.FROM_HTML_MODE_LEGACY)); // Por si viene con entidades HTML

            // Limpiar selección de RadioButtons
            binding.radioGroup.clearCheck();
        }
    }
    private void siguientePregunta() {
        if (binding.radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Debe seleccionar una respuesta", Toast.LENGTH_SHORT).show();
            return;
        }

        PreguntaBean pregunta = listaPreguntas.get(preguntaActual);
        String respuestaCorrecta = pregunta.getCorrect_answer();

        // Evaluar respuesta
        boolean respuestaUsuario = (binding.radioTrue.isChecked()) ? true : false;
        boolean respuestaCorrectaBoolean = respuestaCorrecta.equalsIgnoreCase("True");

        if (respuestaUsuario == respuestaCorrectaBoolean) {
            correctas++;
        } else {
            incorrectas++;
        }

        preguntaActual++;

        if (preguntaActual < listaPreguntas.size()) {
            mostrarPreguntaActual();
        } else {
            finalizarTrivia();
        }
    }

    private void finalizarTrivia() {
        contadorActivo = false;

        if (tiempoAgotado) {
            // Solo si se acabó el tiempo calculamos sin responder
            sinResponder = listaPreguntas.size() - (correctas + incorrectas);
        } else {
            sinResponder = 0;
        }

        Intent intent = new Intent(this, EstadisticasActivity.class);
        intent.putExtra("correctas", correctas);
        intent.putExtra("incorrectas", incorrectas);
        intent.putExtra("sinResponder", sinResponder);
        startActivity(intent);
        finish();
    }



    private String getNombreCategoria(int categoriaId) {
        switch (categoriaId) {
            case 9: return "General Knowledge";
            case 10: return "Entertainment: Books";
            case 11: return "Entertainment: Film";
            case 12: return "Entertainment: Music";
            case 17: return "Science & Nature";
            case 18: return "Science: Computers";
            // Agrega más categorías si quieres
            default: return "Categoría desconocida";
        }
    }


    private void iniciarContador() {
        int segundosPorPregunta;

        switch (dificultad.toLowerCase()) {
            case "easy":
                segundosPorPregunta = 5;
                break;
            case "medium":
                segundosPorPregunta = 7;
                break;
            case "hard":
                segundosPorPregunta = 10;
                break;
            default:
                segundosPorPregunta = 5;
        }

        tiempoTotalSegundos = cantidadPreguntas * segundosPorPregunta;

        contadorHilo = new Thread(() -> {
            while (contadorActivo && tiempoTotalSegundos > 0) {
                runOnUiThread(() -> binding.txtTiempoRestante.setText("00:" + String.format("%02d", tiempoTotalSegundos)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tiempoTotalSegundos--;
            }

            if (contadorActivo) {
                runOnUiThread(() -> {
                    tiempoAgotado = true;
                    finalizarTrivia();
                });
            }

        });
        contadorHilo.start();
    }






    private void cargarPreguntas(int cantidad, int categoria, String dificultad) {
        TriviaService.triviaService.getTrivia(cantidad, categoria, dificultad, "boolean")
                .enqueue(new Callback<TriviaResponse>() {
                    @Override
                    public void onResponse(Call<TriviaResponse> call, Response<TriviaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listaPreguntas = response.body().getResults();
                            iniciarTrivia();
                        } else {
                            Toast.makeText(TriviaActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TriviaResponse> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(TriviaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}




