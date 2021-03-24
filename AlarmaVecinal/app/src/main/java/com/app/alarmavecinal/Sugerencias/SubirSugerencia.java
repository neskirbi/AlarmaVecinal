package com.app.alarmavecinal.Sugerencias;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SubirSugerencia extends AppCompatActivity {
    Funciones funciones;
    Context context;
    TextView de;
    EditText sugerencia;
    String nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_sugerencia);
        context=this;
        funciones=new Funciones(context);
        nombre=funciones.GetNombre();
        de=findViewById(R.id.de);
        sugerencia=findViewById(R.id.sugerencia);

        de.setText("De: "+nombre);
    }

    public void Enviar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        funciones.Conexion("{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"sugerencia\":\""+funciones.ToBase64(sugerencia.getText().toString())+"\"}",funciones.GetUrl()+getString(R.string.url_SetSugerencia),"POST");
        Toast.makeText(context, "Gracias por su sugerencia,", Toast.LENGTH_SHORT).show();
        finish();


    }
}
