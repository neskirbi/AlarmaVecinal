package com.app.alarmavecinal;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.app.alarmavecinal.LoginPack.Login;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Portada extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);
        Empezar();
    }

    void Empezar(){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(Portada.this, Login.class);
                startActivity(intent);
                finish();
            };
        }, 1000);
    }
}
