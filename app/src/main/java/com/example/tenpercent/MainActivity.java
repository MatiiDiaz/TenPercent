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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MediaPlayer musicaVictoria, musicaFondo;
    private TextView tv2;
    public Button button, button2;
    private GestureDetector gestos;
    public ImageButton imageButton, imageButton2, imageButton3;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener evento;
    private boolean n = false;
    private int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicaFondo = MediaPlayer.create(this, R.raw.giovanni_giorgio);
        musicaVictoria = MediaPlayer.create(this, R.raw.siuu);
        button=findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        tv2=findViewById(R.id.tv2);
        gestos = new GestureDetector(this, new ListenerGestos());
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        button.setEnabled(true);
        button2.setEnabled(false);
        musicaFondo.start();
        evento = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    Resources res = getResources();
                    String acceleration = res.getString(R.string.agitacion);
                    if ((event.values[0]>5 || event.values[0]<-5) && n && contador==0){
                        contador++;
                    } else {
                        if ((event.values[0]>5 || event.values[0]<-5) && n && contador==1){
                            musicaFondo.pause();
                            musicaVictoria.start();
                            contador=0;
                            tv2.setText("Agitación");
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(evento, accelerometer, SensorManager.SENSOR_DELAY_UI);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                button2.setEnabled(true);
                n=true;
                musicaVictoria.stop();
                musicaFondo.start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(true);
                button2.setEnabled(false);
                n=false;
                musicaFondo.stop();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class ListenerGestos extends GestureDetector.SimpleOnGestureListener{
        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            if(n){
                musicaFondo.pause();
                musicaVictoria.start();
                musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        musicaFondo.start();
                    }
                });
                tv2.setText("Presion Larga");
            }
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            if(n){
                musicaFondo.stop();
                musicaVictoria.start();
                musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        musicaFondo.start();
                    }
                });
                tv2.setText("Doble Tap");
            }
            return true;
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            if(n){
                if(e2.getX()>e1.getX()){
                    if(e2.getY()>e1.getY()) {
                        musicaFondo.pause();
                        musicaVictoria.start();
                        musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                musicaFondo.start();
                            }
                        });
                        tv2.setText("Desplazamiento a:\nAbajo/Derecha");
                    } else {
                        musicaFondo.pause();
                        musicaVictoria.start();
                        musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                musicaFondo.start();
                            }
                        });
                        tv2.setText("Desplazamiento a:\nArriba/Derecha");
                    }
                } else {
                    if(e2.getY()>e1.getY()) {
                        musicaFondo.pause();
                        musicaVictoria.start();
                        musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                musicaFondo.start();
                            }
                        });
                        tv2.setText("Desplazamiento a:\nAbajo/Izquierda");
                    } else {
                        musicaFondo.pause();
                        musicaVictoria.start();
                        musicaVictoria.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                musicaFondo.start();
                            }
                        });
                        tv2.setText("Desplazamiento a:\nArriba/Izquierda");
                    }
                }
            }
            return true;
        }

    }

    public void music(View view) {
        switch (view.getId()){
            case R.id.imageButton:
                if (musicaVictoria == null){
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
                if(musicaVictoria != null){
                    musicaFondo.pause();
                }
                break;
            case R.id.imageButton3:
                if (musicaVictoria == null){
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
        musicaVictoria.release();
        musicaVictoria = null;
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopMusic();
    }
}