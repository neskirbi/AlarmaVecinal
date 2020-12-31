package com.app.alarmavecinal.FuncionAlertas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.Adapters.AdapterAlertas;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Alertas;
import com.app.alarmavecinal.Estructuras.AlertasL;
import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NewAlerta extends AppCompatActivity implements AdapterAlertas.RecyclerItemClick {
    Funciones funciones;
    ImageView agregar;
    RecyclerView alertas_lista;
    ArrayList<Alertas> alertas = new ArrayList();
    Context context;
    ProgressDialog dialog;
    FirebaseDatabase firebaseDatabaseAlerta;
    DatabaseReference databaseReferenceAlerta;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alerta);
        context=this;
        funciones=new Funciones(context);

        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            funciones.Logo("anuncio","debug");
        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);

        agregar=findViewById(R.id.agregar);




    }

    @Override
    protected void onResume() {
        super.onResume();
        funciones.Vibrar(funciones.VibrarPush());
        Descarga descarga=new Descarga();
        descarga.executeOnExecutor(threadPoolExecutor);
    }

    public void Agregar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        startActivity(new Intent(context,NewAlerta.class));
    }


    public void Descargar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        Descarga descarga=new Descarga();
        descarga.executeOnExecutor(threadPoolExecutor);
    }



    class Descarga extends AsyncTask {
        @Override
        protected void onPreExecute() {

            dialog=ProgressDialog.show(context, "",
                    "Cargando...", true);
            dialog.setCancelable(true);
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(dialog!=null)
                dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            Cargar();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            alertas.clear();

            String data="{\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}";
            String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetPreAlertas));
            try {

                JSONArray jsonArray=new JSONArray(respuesta);
                if(jsonArray.length()!=0){
                    for (int i =0; i < jsonArray.length();i++){
                        JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                        alertas.add(new Alertas(jsonObject.get("id_alerta").toString(),jsonObject.get("imagen").toString(),jsonObject.get("asunto").toString(),jsonObject.get("fecha").toString(),jsonObject.toString()));

                    }


                }
            } catch (JSONException e) {
                funciones.Logo("Catch","Error: "+e.getMessage());
            }
            publishProgress();
            return null;
        }
    }

    void Cargar(){

        alertas_lista=findViewById(R.id.alertas_lista);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        alertas_lista.setLayoutManager(mLayoutManager);
        alertas_lista.setAdapter(new AdapterAlertas(alertas,this));
        if(alertas.size()==0){
            funciones.VerificaConexion();
        }


    }


    public void Pop(final String json) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage("¿Enviar Alerta?");



        alertDialog.setIcon(R.drawable.img_menu_grupo);

        alertDialog.setPositiveButton("Enviar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Enviar(json);

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void Enviar(String json){
        firebaseDatabaseAlerta = FirebaseDatabase.getInstance();
        databaseReferenceAlerta = firebaseDatabaseAlerta.getReference("Alertas-"+funciones.GetIdGrupo());//Sala de chat
        try {

            JSONObject jsonObject0=new JSONObject(json);
            databaseReferenceAlerta.push().setValue(new AlertasL(funciones.GetUIID(),funciones.GetIdUsuario(),jsonObject0.get("imagen").toString(),jsonObject0.get("asunto").toString(),funciones.GetNombre(),funciones.GetDate(),""));

            String response="", data="{\"id_alerta\":\""+jsonObject0.get("id_alerta").toString()+"\",\"id_grupo\":\""+funciones.GetIdGrupo()+"\",\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"imagen\":\""+jsonObject0.get("imagen").toString()+"\",\"asunto\":\""+jsonObject0.get("asunto").toString()+"\",\"mensaje\":\"\"}";

            String url =funciones.GetUrl()+getString(R.string.url_SetAlertas);
            response=funciones.Conexion(data,url);

            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.get("respuesta").toString().contains("1")){
                Toast.makeText(context, "¡Se guardo!", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(context, "¡Error al guardar!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void itemClick(Alertas alertas) {
        funciones.Vibrar(funciones.VibrarPush());
        Pop(alertas.getJson());
    }


}
