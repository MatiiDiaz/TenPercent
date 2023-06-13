package com.example.tenpercent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer musicaVictoria, musicaFondo, musicaDerrota;
    private TextView tv2, tvPuntos;
    private Button button, button2;
    private GestureDetector gestos;
    public ImageButton imageButton, imageButton2, imageButton3;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener evento;
    private boolean juegoIniciado = false;
    private int puntos = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicaFondo = MediaPlayer.create(this, R.raw.giovanni_giorgio);
        musicaVictoria = MediaPlayer.create(this, R.raw.siuu);
        musicaDerrota = MediaPlayer.create(this, R.raw.no_god_please_no);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        tv2 = findViewById(R.id.tv2);
        tvPuntos = findViewById(R.id.tvPuntos);
        gestos = new GestureDetector(this, new ListenerGestos());
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
                        musicaVictoria.start();

                        incrementarPuntos();
                        actualizarAccionActual();
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
            tvPuntos.setText(String.valueOf(puntos));

            if (!musicaFondo.isPlaying()) {
                musicaFondo.start();
            }

            iniciarNuevaAccion();
        }
    }

    private void detenerJuego() {
        if (juegoIniciado) {
            juegoIniciado = false;
            button.setEnabled(true);
            button2.setEnabled(false);

            detenerMusica();

            mostrarPuntos();
            reiniciarJuego();
        }
    }

    private void iniciarNuevaAccion() {
        accionActualId = obtenerAccionAleatoria();
        tv2.setText(accionActualId);
    }

    private int obtenerAccionAleatoria() {
        int randomIndex = random.nextInt(accionIdArray.length);
        return accionIdArray[randomIndex];
    }

    private void actualizarAccionActual() {
        if (juegoIniciado) {
            accionActualId = obtenerAccionAleatoria();
            tv2.setText(accionActualId);
        }
    }

    private void incrementarPuntos() {
        puntos++;
        tvPuntos.setText(String.valueOf(puntos));
    }

    private void detenerMusica() {
        if (musicaFondo.isPlaying()) {
            musicaFondo.stop();
        }
        if (musicaVictoria.isPlaying()) {
            musicaVictoria.stop();
        }
    }

    private void mostrarPuntos() {
        // Detenemos la música si está reproduciéndose
        detenerMusica();

        // Mostramos los puntos obtenidos
        String mensajePuntos = ("Puntaje: " + puntos);
        tv2.setText(mensajePuntos);
    }

    private void reiniciarJuego() {
        puntos = 0;
        tvPuntos.setText(String.valueOf(puntos));
        tvPuntos.setText(R.string.musica);
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
            musicaFondo.start();
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
    }

    class ListenerGestos extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.simple_tap) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
            }
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.presion_larga) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
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
                }
            }
            return true;
        }
    }

    public void music(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                if (musicaVictoria == null) {
                    musicaVictoria = MediaPlayer.create(this, R.raw.siuu);
                }
                musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopMusic();
                    }
                });
                musicaVictoria.start();
                break;
            case R.id.imageButton2:
                if (musicaVictoria != null) {
                    musicaFondo.pause();
                }
                break;
            case R.id.imageButton3:
                if (musicaVictoria == null) {
                    musicaVictoria = MediaPlayer.create(this, R.raw.siuu);
                }
                musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        musicaVictoria.start();
                    }
                });
                musicaVictoria.start();
                break;
        }
    }

    private void stopMusic() {
        if (musicaVictoria != null) {
            musicaVictoria.release();
            musicaVictoria = null;
        }
    }
}
