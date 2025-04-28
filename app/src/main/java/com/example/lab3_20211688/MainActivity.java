package com.example.lab3_20211688;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lab3_20211688.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    EditText campoCategoria, campoCantidad, campoDificultad;
    Button btnComenzar;
    Boolean hayInternet=false; // cambia a verdadero solo luego de comprobar la conexion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Para el dropdown de cateogrías
        String[] listaCategorias = {"Cultura General", "Libros", "Peliculas","Musica","Computacion","Matematica","Deportes","Historia"};
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                listaCategorias
        );
        binding.listaCategorias.setAdapter(adapterCategorias);

        //Para el dropdown de dificultades
        String[] listaDificultad = {"facil", "medio", "difícil"};
        ArrayAdapter<String> adapterDificultad = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                listaDificultad
        );
        binding.listaDificultades.setAdapter(adapterDificultad);



        //logica para habilitar el boton comenzar
        campoCategoria = binding.spinnerCategoria.getEditText();
        campoCantidad = binding.editCantidad;
        campoDificultad = binding.spinnerDificultad.getEditText();
        btnComenzar = binding.btnComenzar;

        //Log.d("campoCategoria",campoCategoria.getText().toString());
        //Log.d("campoCantidad",campoCantidad.getText().toString());
        //Log.d("campoDificultad",campoDificultad.getText().toString());


        // Agregamos el mismo TextWatcher a los 3 campos
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                verificarCampos();
            }
        };

        campoCategoria.addTextChangedListener(textWatcher);
        campoCantidad.addTextChangedListener(textWatcher);
        campoDificultad.addTextChangedListener(textWatcher);


        // Inicialmente botón en gris
        btnComenzar.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        btnComenzar.setEnabled(false);

    }


    private void verificarCampos() {
        String texto1 = campoCategoria.getText().toString().trim();
        String texto2 = campoCantidad.getText().toString().trim();
        String texto3 = campoDificultad.getText().toString().trim();



        if (!texto1.isEmpty() && !texto2.isEmpty() && !texto3.isEmpty() && hayInternet ) {

            btnComenzar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.verde_owo));
            btnComenzar.setEnabled(true);
        } else {

            btnComenzar.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
            btnComenzar.setEnabled(true);
        }
    }

    public void comprobarInternet(View view){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        hayInternet = isConnected;
        Log.d("conectado"," " + isConnected);
        if (isConnected) {
            verificarCampos();
            Toast.makeText(this, "Exit Toast", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error Toast", Toast.LENGTH_SHORT).show();
        }

    }

    public void botonComenzar(View view) {
        // Obtener valores de los EditText
        String categoriaTexto = campoCategoria.getText().toString().trim();
        String cantidadTexto = campoCantidad.getText().toString().trim();
        String dificultadTexto = campoDificultad.getText().toString().trim();

        Log.d("categoria", categoriaTexto);
        Log.d("cantidad", cantidadTexto);
        Log.d("dificultad", dificultadTexto);

        // Validar que no estén vacíos
        if (categoriaTexto.isEmpty() || cantidadTexto.isEmpty() || dificultadTexto.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Primero convertir cantidad (cantidad SI debe ser número)
                int cantidad = Integer.parseInt(cantidadTexto);

                // Convertir dificultad de español a inglés
                String dificultad;
                switch (dificultadTexto.toLowerCase()) {
                    case "fácil":
                        dificultad = "easy";
                        break;
                    case "medio":
                        dificultad = "medium";
                        break;
                    case "difícil":
                        dificultad = "hard";
                        break;
                    default:
                        dificultad = "easy"; // Por defecto
                        break;
                }

                // Convertir categoría de texto a ID
                int categoria;
                switch (categoriaTexto.toLowerCase()) {
                    case "musica":
                    case "música":
                        categoria = 12; // Entertainment: Music
                        break;
                    case "cine":
                    case "peliculas":
                    case "películas":
                        categoria = 11; // Entertainment: Film
                        break;
                    case "libros":
                        categoria = 10; // Entertainment: Books
                        break;
                    case "ciencia":
                    case "science":
                        categoria = 17; // Science & Nature
                        break;
                    case "computadoras":
                    case "computers":
                        categoria = 18; // Science: Computers
                        break;
                    default:
                        categoria = 9; // General Knowledge (por defecto)
                        break;
                }

                // Validar que cantidad sea positiva
                if (cantidad <= 0) {
                    Toast.makeText(this, "La cantidad debe ser positiva", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mostrar datos en Log
                Log.d("categoria", "" + categoria);
                Log.d("cantidad", "" + cantidad);
                Log.d("dificultad", dificultad);

                // Lanzar la TriviaActivity pasando parámetros
                Intent intent = new Intent(this, TriviaActivity.class);
                intent.putExtra("categoria", categoria);
                intent.putExtra("cantidad", cantidad);
                intent.putExtra("dificultad", dificultad);
                startActivity(intent);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Debe ingresar un número válido en cantidad", Toast.LENGTH_SHORT).show();
            }
        }
    }





}
