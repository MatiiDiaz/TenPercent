package com.example.tenpercent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvPuntajeCarrera, tvPuntajeLegendario;
    private static final String KEY_PUNTAJE_CARRERA = "key_puntaje_carrera";
    private static final String KEY_PUNTAJE_LEGENDARIO = "key_puntaje_legendario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        tvPuntajeCarrera = findViewById(R.id.tvPuntajeCarrera);
        tvPuntajeLegendario = findViewById(R.id.tvPuntajeLegendario);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        int puntajeCarrera = sharedPreferences.getInt(KEY_PUNTAJE_CARRERA, 0);
        int puntajeLegendario = sharedPreferences.getInt(KEY_PUNTAJE_LEGENDARIO, 0);

        String mensajePuntajeCarrera = getString(R.string.msj_puntaje_carrera) + " " + puntajeCarrera;
        String mensajePuntajeLegendario = getString(R.string.msj_puntaje_legendario) + " " + puntajeLegendario;

        tvPuntajeCarrera.setText(mensajePuntajeCarrera);
        tvPuntajeLegendario.setText(mensajePuntajeLegendario);
    }
}
