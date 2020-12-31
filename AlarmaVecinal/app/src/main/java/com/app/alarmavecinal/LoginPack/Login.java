package com.app.alarmavecinal.LoginPack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
        if(PedirPermiso()) {
            Validar();
        }
    }

    public void Validar(){
        enviar.setEnabled(false);
        Boolean key1=false,key2=false;
        funciones.Vibrar(funciones.VibrarPush());

        if(mail.getText().toString().replace(" ","").length()==0 && pass.getText().toString().replace(" ","").length()==0){
            funciones.Vibrar(funciones.VibrarError());
            Toast.makeText(this, "Ingresar los datos.", Toast.LENGTH_SHORT).show();
        }else{
            String data="{\"mail\":\""+mail.getText().toString()+"\",\"pass\":\""+pass.getText().toString()+"\",\"fbtoken\":\""+ FirebaseInstanceId.getInstance().getToken()+""+"\"}";
            String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetOneUser));
            try {

                JSONArray jsonArray=new JSONArray(respuesta);
                if(jsonArray.length()!=0){
                    JSONObject jsonObject=new JSONObject(jsonArray.get(0).toString());
                    if(jsonObject.getString("mail").contains(mail.getText().toString())){
                        key1=true;
                        tilmail.setError(null);
                    }else{
                        tilmail.setError("¡Dato Incorrecto!");
                    }

                    if(jsonObject.getString("pass").contains(pass.getText().toString())){
                        key2=true;
                        tilpass.setError(null);
                    }
                    else{
                        tilpass.setError("¡Dato Incorrecto!");
                    }

                    if(key1 && key2){
                        funciones.CreaLogin(jsonObject);
                        funciones.Logo("errorqr",jsonObject.getString("id_grupo")+"=="+jsonObject.getString("id_grupo").length());
                        if(jsonObject.getString("id_grupo").length()==32){
                            funciones.GuardarGrupo2(jsonObject.getString("id_grupo"));
                        }else{
                            Toast.makeText(context, "No te has unido a un grupo aun.", Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(getApplicationContext(),Principal.class));
                    }
                }else{
                    Toast.makeText(this, "¡Datos Incorrectos!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        enviar.setEnabled(true);

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


}
