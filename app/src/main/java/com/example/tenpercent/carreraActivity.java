package com.example.tenpercent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class carreraActivity extends AppCompatActivity {

    private MediaPlayer musicaVictoria, musicaFondo, musicaDerrota;
    private TextView tv2, tvPuntos;
    private Button button, button2;
    private GestureDetector gestos;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener evento;
    private boolean juegoIniciado = false;
    private int puntos = 0;
    private int vidas = 3;
    private TextView tvVidas;
    private CountDownTimer timer;
    private long tiempoRestante = 5000; // 5 segundos en milisegundos
    private static final int LIMITE_PUNTOS_1 = 5;
    private static final int LIMITE_PUNTOS_2 = 10;
    private static final long TIEMPO_INICIAL = 5000; // 5 segundos en milisegundos
    private static final long TIEMPO_NIVEL_1 = 3000; // 4 segundos en milisegundos
    private static final long TIEMPO_NIVEL_2 = 1500; // 3 segundos en milisegundos

    private static final String KEY_PUNTAJE_CARRERA = "key_puntaje_carrera";



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
        setContentView(R.layout.activity_carrera);

        musicaFondo = MediaPlayer.create(this, R.raw.giovanni_giorgio);
        musicaVictoria = MediaPlayer.create(this, R.raw.siuu);
        musicaDerrota = MediaPlayer.create(this, R.raw.no_god_please_no);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        tv2 = findViewById(R.id.tv2);
        tvPuntos = findViewById(R.id.tvPuntos);
        tvVidas = findViewById(R.id.tvVidas);
        gestos = new GestureDetector(this, new carreraActivity.ListenerGestos());
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
            String mensajePuntos = ("Puntaje: " + puntos);
            tvPuntos.setText(mensajePuntos);
            String mensajeVidas = ("Vidas: " + vidas);
            tvVidas.setText(mensajeVidas);

            if (!musicaFondo.isPlaying()) {
                musicaFondo.start();
            }

            actualizarAccionActual();
            reiniciarTemporizador();
        }
    }

    private void restarVida() {
        vidas--;
        String mensajeVidas = ("Vidas: " + vidas);
        tvVidas.setText(mensajeVidas);

        if (vidas <= 0 && juegoIniciado) {
            timer.cancel();
            detenerJuego();
        } else {
            reiniciarTemporizador(); // Agregar esta línea para reiniciar el temporizador después de restar una vida
            actualizarAccionActual(); // Agregar esta línea para mostrar una nueva acción después de restar una vida
        }
    }

    private void detenerJuego() {
        juegoIniciado = false;
        button.setEnabled(true);
        button2.setEnabled(false);
        detenerMusica();
        vidas = 3;
        String mensajeVidas = ("Vidas: " + vidas);
        tvVidas.setText(mensajeVidas);
        // Verificar si el puntaje es mayor que el valor almacenado en las preferencias
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int puntajeAlmacenado = sharedPreferences.getInt(KEY_PUNTAJE_CARRERA, 0);
        if (puntos > puntajeAlmacenado) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_PUNTAJE_CARRERA, puntos);
            editor.apply();
            mostrarFinDeJuego(puntos);
        } else {
            mostrarFinDeJuego(puntajeAlmacenado);
        }
        puntos = 0;
    }

    private void reiniciarTemporizador() {
        if (timer != null) {
            timer.cancel();
        }

        // Actualizar tiempoRestante según los límites de puntos alcanzados
        if (puntos >= LIMITE_PUNTOS_2) {
            tiempoRestante = TIEMPO_NIVEL_2;
        } else if (puntos >= LIMITE_PUNTOS_1) {
            tiempoRestante = TIEMPO_NIVEL_1;
        } else {
            tiempoRestante = TIEMPO_INICIAL;
        }

        timer = new CountDownTimer(tiempoRestante, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                restarVida();
            }
        };

        timer.start();
    }

    private void mostrarFinDeJuego(int puntajeMaximo) {
        String mensajeFinDeJuego = "Puntos obtenidos: " + puntos;
        mensajeFinDeJuego += "\nPuntaje máximo: " + puntajeMaximo;
        tv2.setText(mensajeFinDeJuego);
    }


    private int obtenerAccionAleatoria() {
        int randomIndex = random.nextInt(accionIdArray.length);
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
        String mensajePuntos = ("Puntaje: " + puntos);
        tvPuntos.setText(mensajePuntos);
        // Actualizar tiempoRestante según los límites de puntos alcanzados
        if (puntos >= LIMITE_PUNTOS_2) {
            tiempoRestante = TIEMPO_NIVEL_2;
            Log.d("a","Nivel 3");
        } else if (puntos >= LIMITE_PUNTOS_1) {
            tiempoRestante = TIEMPO_NIVEL_1;
            Log.d("a","Nivel 2");
        }
        // Reiniciar el temporizador con el nuevo tiempoRestante
        reiniciarTemporizador();
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
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.simple_tap) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
                reiniciarTemporizador();
            } else if (juegoIniciado) {
                musicaDerrota.start();
                restarVida();
            }
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.presion_larga) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
                reiniciarTemporizador();
            } else if (juegoIniciado) {
                musicaDerrota.start();
                restarVida();
            }
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            if (juegoIniciado && accionActualId == R.string.doble_tap) {
                musicaVictoria.start();

                incrementarPuntos();
                actualizarAccionActual();
                reiniciarTemporizador();
            } else if (juegoIniciado) {
                musicaDerrota.start();
                restarVida();
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
                    restarVida();
                } else if (e2.getY() < e1.getY() && accionActualId != R.string.deslizar_arriba) {
                    musicaDerrota.start();
                    restarVida();
                }

                if (acierto) {
                    musicaVictoria.start();
                    incrementarPuntos();
                    actualizarAccionActual();
                    reiniciarTemporizador();
                }
            }
            return true;
        }
    }
}