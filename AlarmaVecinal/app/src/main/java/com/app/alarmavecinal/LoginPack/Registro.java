package com.app.alarmavecinal.LoginPack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.alarmavecinal.Principal;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    EditText nombres,apellidos,direccion,mail,pass1,pass2;
    Context context;
    Funciones funciones;
    ProgressDialog dialog;

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
            dialog = ProgressDialog.show(Registro.this, "", "Registrando...", true);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url =funciones.GetUrl()+getString(R.string.url_registrar);

            // Request a string response from the provided URL.
            try {
                JSONObject data= new JSONObject("{\"nombres\":\""+n+"\",\"apellidos\":\""+a+"\",\"direccion\":\""+d+"\",\"mail\":\""+m+"\",\"pass\":\""+p1+"\"}");
                funciones.Logo("registro",data.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,data,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dialog.dismiss();
                                funciones.Logo("registro",response.toString());

                                try {

                                    funciones.CreaLogin(response);

                                    if(response.getString("id_grupo").length()==32){
                                        funciones.GuardarGrupoLogin(response.getString("id_grupo"),
                                                response.getString("id_usuario_grupo"),
                                                response.getString("nombre_grupo"));
                                    }else{
                                        Toast.makeText(context, "No te has unido a un grupo aun.", Toast.LENGTH_SHORT).show();
                                    }

                                    startActivity(new Intent(getApplicationContext(), Principal.class));
                                } catch (JSONException e) {
                                    funciones.Logo("registro",e.getMessage().toString());
                                }

                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        funciones.Logo("registro","That didn't work!: "+error.getMessage());
                        Toast.makeText(getApplicationContext(), "¡Error de conexion!", Toast.LENGTH_SHORT).show();

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                funciones.Logo("registro","That didn't work!: "+e.getMessage());
            }



        }


    }


}
