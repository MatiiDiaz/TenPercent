package com.example.tenpercent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {
    private ImageView imageView;
    protected void onCreate(Bundle savedInstanceState){
        // Antes de la llamada a super.onCreate(), establece el tema según el modo actual
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Obtén la referencia al ImageView
        imageView = findViewById(R.id.imageView3);

        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        Timer tiempo = new Timer();
        tiempo.schedule(tarea, 2000);
    }
}