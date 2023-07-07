package com.example.tenpercent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnModoCarrera, btnModoLegendario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnModoCarrera = findViewById(R.id.btnModoCarrera);
        btnModoLegendario = findViewById(R.id.btnModoLegendario);

        btnModoCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, carreraActivity.class);
                startActivity(intent);
            }
        });

        btnModoLegendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, carreraActivity.class);
                startActivity(intent);
            }
        });
    }
}

