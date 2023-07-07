package com.example.tenpercent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.IconCompat;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class legendarioActivity extends AppCompatActivity {

    private MediaPlayer musicaVictoria, musicaFondo, musicaDerrota;
    private TextView tv2, tvPuntos;
    private ImageView ivIcon;
    private Button button, button2;
    private GestureDetector gestos;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener evento;
    private boolean juegoIniciado = false;
    private int puntos = 0;
    private CountDownTimer timer;
    private long tiempoRestante = 1300; // 1,3 segundos en milisegundos

    private static final String KEY_PUNTAJE_LEGENDARIO = "key_puntaje_legendario";

    private final int[] accionIdArray = {
            R.string.simple_tap,
            R.string.doble_tap,
            R.string.presion_larga,
            R.string.agitacion,
            R.string.deslizar_arriba,
            R.string.deslizar_abajo
    };
    private int accionActualId;
    private Random random;
    private IconCompat[] iconIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legendario);

        iconIdArray = new IconCompat[] {
                IconCompat.createWithResource(this, R.drawable.tap_icon),
                IconCompat.createWithResource(this, R.drawable.double_tap_icon),
                IconCompat.createWithResource(this, R.drawable.long_press_icon),
                IconCompat.createWithResource(this, R.drawable.shake_icon),
                IconCompat.createWithResource(this, R.drawable.swipe_up_icon),
                IconCompat.createWithResource(this, R.drawable.swipe_down_icon)
        };

        musicaFondo = MediaPlayer.create(this, R.raw.giovanni_giorgio);
        musicaVictoria = MediaPlayer.create(this, R.raw.siuu);
        musicaDerrota = MediaPlayer.create(this, R.raw.no_god_please_no);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        ivIcon = findViewById(R.id.ivIcon);
        tv2 = findViewById(R.id.tv2);
        tvPuntos = findViewById(R.id.tvPuntos);
        gestos = new GestureDetector(this, new legendarioActivity.ListenerGestos());
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        random = new Random();

        button.setEnabled(true);
        button2.setEnabled(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarJuego();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerJuego();
            }
        });

        evento = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && juegoIniciado) {
                    if ((event.values[0] > 8 || event.values[0] < -8) && accionActualId == R.string.agitacion) {
                        incrementarPuntos();
                        actualizarAccionActual();
                        iniciarTemporizador();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    private void iniciarJuego() {
        if (!juegoIniciado) {
            juegoIniciado = true;
            button.setEnabled(false);
            button2.setEnabled(true);
            String mensajePuntos = getString(R.string.puntaje) + " " + puntos;
            tvPuntos.setText(mensajePuntos);
            tvPuntos.setVisibility(View.VISIBLE);
            iniciarMusica();
            actualizarAccionActual();
            iniciarTemporizador();
        }
    }

    private void detenerJuego() {
        juegoIniciado = false;
        button.setEnabled(true);
        button2.setEnabled(false);
        detenerMusica();
        // Verificar si el puntaje es mayor que el valor almacenado en las preferencias
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int puntajeAlmacenado = sharedPreferences.getInt(KEY_PUNTAJE_LEGENDARIO, 0);
        if (puntos > puntajeAlmacenado) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_PUNTAJE_LEGENDARIO, puntos);
            editor.apply();
            mostrarFinDeJuego(puntos);
        } else {
            mostrarFinDeJuego(puntajeAlmacenado);
        }
        puntos = 0;
        if (timer != null) {
            timer.cancel();
        }
    }

    private void iniciarTemporizador() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(tiempoRestante, 1300) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                detenerJuego();
            }
        };

        timer.start();
    }

    private void mostrarFinDeJuego(int puntajeMaximo) {
        String mensajeFinDeJuego = getString(R.string.puntos_obtenidos2) + " " + puntos;
        mensajeFinDeJuego += getString(R.string.puntaje_maximo) + " " + puntajeMaximo;
        tv2.setText(mensajeFinDeJuego);
        tvPuntos.setVisibility(View.INVISIBLE);
    }

    private int obtenerAccionAleatoria() {
        int randomIndex = random.nextInt(accionIdArray.length);
        Drawable iconDrawable = iconIdArray[randomIndex].loadDrawable(this);
        ivIcon.setImageDrawable(iconDrawable);
        return accionIdArray[randomIndex];
    }

    private void actualizarAccionActual() {
        if (juegoIniciado) {
            accionActualId = obtenerAccionAleatoria();
            tv2.setText(getString(accionActualId));
        }
    }

    private void incrementarPuntos() {
        puntos++;
        String mensajePuntos = getString(R.string.puntaje) + " " + puntos;
        tvPuntos.setText(mensajePuntos);
        if (puntos % 10 == 0) {
            musicaVictoria.start();
        }
    }

    private void iniciarMusica() {
        if (!musicaFondo.isPlaying()) {
            musicaFondo.start();
        }
        if (musicaVictoria.isPlaying()) {
            musicaVictoria.stop();
        }
        if (musicaDerrota.isPlaying()) {
            musicaDerrota.stop();
        }
    }

    private void detenerMusica() {
        if (musicaFondo.isPlaying()) {
            musicaFondo.stop();
        }
        if (musicaVictoria.isPlaying()) {
            musicaVictoria.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(evento);
        detenerMusica();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(evento, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (juegoIniciado && !musicaFondo.isPlaying()) {
            iniciarMusica();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicaFondo != null) {
            musicaFondo.release();
        }
        if (musicaVictoria != null) {
            musicaVictoria.release();
        }
        if (musicaDerrota != null) {
            musicaDerrota.release();
        }
    }

    class ListenerGestos extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId ==R.string.simple_tap) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
                iniciarTemporizador();
            } else if (juegoIniciado){
                musicaDerrota.start();
                detenerJuego();
            }
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.presion_larga) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
                iniciarTemporizador();
            } else if (juegoIniciado) {
                musicaDerrota.start();
                detenerJuego();
            }
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.doble_tap) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
                iniciarTemporizador();
            } else if (juegoIniciado) {
                musicaDerrota.start();
                detenerJuego();
            }
            return true;
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            if (juegoIniciado) {
                boolean acierto = false;
                if (e2.getY() > e1.getY() && accionActualId == R.string.deslizar_abajo) {
                    acierto = true;
                } else if (e2.getY() < e1.getY() && accionActualId == R.string.deslizar_arriba) {
                    acierto = true;
                } else if (e2.getY() > e1.getY() && accionActualId != R.string.deslizar_abajo) {
                    musicaDerrota.start();
                    detenerJuego();
                } else if (e2.getY() < e1.getY() && accionActualId != R.string.deslizar_arriba) {
                    musicaDerrota.start();
                    detenerJuego();
                }

                if (acierto) {
                    musicaVictoria.start();
                    incrementarPuntos();
                    actualizarAccionActual();
                    iniciarTemporizador();
                }
            }
            return true;
        }
    }
}
