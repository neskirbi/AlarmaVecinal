package com.app.alarmavecinal.LoginPack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alarmavecinal.EditarInfo.Datos;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    Button enviar;
    TextView recuperar,registrar;
    EditText mail,pass;
    TextInputLayout tilmail,tilpass;
    Funciones funciones;
    Context context;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;
        funciones=new Funciones(this);
        if(funciones.Check_Log()){
            startActivity(new Intent(getApplicationContext(),Principal.class));
        }

        recuperar=findViewById(R.id.recuperar);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                startActivity(new Intent(getApplicationContext(),Recuperar.class));
            }
        });
        registrar=findViewById(R.id.registrar);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                startActivity(new Intent(getApplicationContext(),Registro.class));
            }
        });
        enviar=findViewById(R.id.enviar);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass);
        tilmail=findViewById(R.id.tilmail);
        tilpass=findViewById(R.id.tilpass);


    }

    public void Enviar(View view){
        funciones.Vibrar(funciones.VibrarPush());

        if(PedirPermiso()) {
            Login();
        }
    }



    @Override
    public void onBackPressed() {


    }

    public Boolean PedirPermiso() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};

            int sd = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (sd == PackageManager.PERMISSION_GRANTED ) {

                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }else{
        }
        return true;
    }

    public void Login(){

        if(mail.getText().toString().replace(" ","").length()==0 && pass.getText().toString().replace(" ","").length()==0){
            Toast.makeText(this, "Ingresar los datos.", Toast.LENGTH_SHORT).show();
        }else{
            dialog = ProgressDialog.show(Login.this, "", "Verificando...", true);
            enviar.setEnabled(false);
            funciones.AbrirConexion();

            RequestQueue queue = Volley.newRequestQueue(this);
            String url =funciones.GetUrl()+getString(R.string.url_login)+"/"+mail.getText().toString()+"/"+pass.getText().toString()+"";
            funciones.Logo("login",url);
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            enviar.setEnabled(true);
                            // Display the first 500 characters of the response string.
                            funciones.Logo("login",response);
                            try {

                                JSONArray jsonArray=new JSONArray(response);
                                if(jsonArray.length()!=0){
                                    JSONObject jsonObject=new JSONObject(jsonArray.get(0).toString());

                                    funciones.CreaLogin(jsonObject);

                                    if(jsonObject.getString("id_grupo").length()==32){
                                        funciones.GuardarGrupoLogin(jsonObject.getString("id_grupo"),
                                                jsonObject.getString("id_usuario_grupo"),
                                                jsonObject.getString("nombre_grupo"));
                                    }else{
                                        Toast.makeText(context, "No te has unido a un grupo aun.", Toast.LENGTH_SHORT).show();
                                    }

                                    startActivity(new Intent(getApplicationContext(),Principal.class));
                                }else{
                                    Toast.makeText(getApplicationContext(), "¡Datos Incorrectos!", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                funciones.Logo("login",e.getMessage());

                            }

                        }


                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    funciones.Logo("login","That didn't work!");
                    Toast.makeText(getApplicationContext(), "¡Error de conexion!", Toast.LENGTH_SHORT).show();
                    enviar.setEnabled(true);
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


    }


}
