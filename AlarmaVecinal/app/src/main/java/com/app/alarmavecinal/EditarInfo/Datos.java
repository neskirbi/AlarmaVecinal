package com.app.alarmavecinal.EditarInfo;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.LoginPack.Login;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Datos extends AppCompatActivity {
    EditText nombres,apellidos,direccion,pass;
    Context context;
    Funciones funciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        setTitle("Editar Info.");
        context=this;
        funciones=new Funciones(context);

        nombres=findViewById(R.id.nombre);
        apellidos=findViewById(R.id.apellidos);
        direccion=findViewById(R.id.direccion);
        pass=findViewById(R.id.pass);

        GetInfo();
    }

    private void GetInfo() {
        String data="{\"id_usuario\":\""+funciones.GetIdUsuario()+"\"}";
        String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetInfoToEdit));


        try {

            JSONArray jsonArray=new JSONArray(respuesta);
            if(jsonArray.length()!=0){
                JSONObject jsonObject=new JSONObject(jsonArray.get(0).toString());

                nombres.setText(jsonObject.get("nombres").toString());
                apellidos.setText(jsonObject.get("apellidos").toString());
                direccion.setText(jsonObject.getString("direccion"));
                pass.setText(jsonObject.getString("pass"));
            }else{
                Toast.makeText(this, "¡Datos Incorrectos!", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void Actualizar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        String n=nombres.getText().toString(),a=apellidos.getText().toString(),d=direccion.getText().toString(),p=pass.getText().toString();

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

        if(p.length()==0){
            Toast.makeText(context, "Debe ingresar su contraseña.", Toast.LENGTH_SHORT).show();
            cont++;
        }

         if(cont==0){
            String data="{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"nombres\":\""+n+"\",\"apellidos\":\""+a+"\",\"direccion\":\""+d+"\",\"pass\":\""+p+"\"}";
            String url =funciones.GetUrl()+getString(R.string.url_SetInfoEdit);
            response=funciones.Conexion(data,url);

            try {
                JSONObject jsonObject=new JSONObject(response);
                if(jsonObject.get("respuesta").toString().contains("1")){
                    Toast.makeText(context, "¡Datos actualizados!", Toast.LENGTH_SHORT).show();
                    funciones.LogOut();
                    startActivity(new Intent(this, Login.class));
                }else{
                    Toast.makeText(context, "¡Error al actualizar al datos!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
