package com.app.alarmavecinal.LoginPack;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    EditText nombres,apellidos,direccion,mail,pass1,pass2;
    Context context;
    Funciones funciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setTitle("Registro");
        context=this;
        funciones=new Funciones(context);

        nombres=findViewById(R.id.nombre);
        apellidos=findViewById(R.id.apellidos);
        direccion=findViewById(R.id.direccion);
        mail=findViewById(R.id.mail);
        pass1=findViewById(R.id.pass1);
        pass2=findViewById(R.id.pass2);
    }

    public void Registrar(View view){

        funciones.Vibrar(funciones.VibrarPush());
        String n=nombres.getText().toString(),a=apellidos.getText().toString(),d=direccion.getText().toString()
                ,m=mail.getText().toString()
                ,p1=pass1.getText().toString(),p2=pass2.getText().toString();

        String response="";
        int cont=0;
        if(n.length()==0){
            Toast.makeText(context, "Debe ingresar su nombre.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(a.length()==0){
            Toast.makeText(context, "Debe ingresar sus apellidos.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(d.length()==0){
            Toast.makeText(context, "Debe ingresar su dirección o numero de casa.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(m.length()==0){
            Toast.makeText(context, "Debe ingresar un correo.", Toast.LENGTH_SHORT).show();
            cont++;
        }else{
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            if (!pattern.matcher(m).matches()){
                Toast.makeText(context, "Email no válido.", Toast.LENGTH_SHORT).show();
                cont++;
            }
        }

        if(p1.length()==0){
            Toast.makeText(context, "Debe ingresar contraseña.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if(p2.length()==0){
            Toast.makeText(context, "Debe ingresar contraseña.", Toast.LENGTH_SHORT).show();
            cont++;
        }

        if (!p1.equals(p2))
        {
            Toast.makeText(context, "Contraseñas diferentes.", Toast.LENGTH_SHORT).show();
            cont++;
        }



        if(cont==0){
            String data="{\"nombres\":\""+n+"\",\"apellidos\":\""+a+"\",\"direccion\":\""+d+"\",\"mail\":\""+m+"\",\"pass\":\""+p1+"\"}";
            String url =funciones.GetUrl()+getString(R.string.url_SetUsuario);
            response=funciones.Conexion(data,url);

            try {
                JSONObject jsonObject=new JSONObject(response);
                if(jsonObject.get("respuesta").toString().contains("1")){
                    Toast.makeText(context, "¡Registro Correcto!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Login.class));
                }else{
                    Toast.makeText(context, "¡Error al agregar al usuario!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
