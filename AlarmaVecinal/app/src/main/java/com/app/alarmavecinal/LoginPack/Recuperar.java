package com.app.alarmavecinal.LoginPack;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Recuperar extends AppCompatActivity {
    EditText mail;
    Funciones funciones;
    Context context;
    TextView mensaje;
    Button enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recuperar);
        context=this;
        funciones=new Funciones(context);
        mensaje = findViewById(R.id.mensaje);
        mail = findViewById(R.id.mail);
        enviar=findViewById(R.id.enviar);


        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);
    }

    public void Enviar(View view){
        enviar.setEnabled(false);
        enviar.setBackground(getDrawable(R.drawable.boton_disable));
        String data="{\"mail\":\""+mail.getText().toString()+"\"}";
        String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_Recuperar));
        if(!respuesta.contains("Error")){
            mensaje.setText("¡Se envió  un correo con la contraseña!,\n Favor de buscarlo en la bandeja principal o en Spam de su correo");

        }else{
            Toast.makeText(context, "¡El correo no esta en la base de datos!", Toast.LENGTH_SHORT).show();
            mensaje.setText("¡El correo no está registrado en la base de datos!");
            enviar.setEnabled(true);
            enviar.setBackground(getDrawable(R.drawable.boton_azul));
        }
    }
}
