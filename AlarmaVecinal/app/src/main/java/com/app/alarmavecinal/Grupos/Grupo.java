package com.app.alarmavecinal.Grupos;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Tabs.AyudaGrupoTab;
import com.app.alarmavecinal.Variado.AyudaGrupo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Grupo extends AppCompatActivity {
    LinearLayout crear,unirse,botones;
    LinearLayout grupo_lay;
    Funciones funciones;
    Context context;
    ImageView QR;
    String SQR="";
    TextView titulogrupo,menu_salir,menu_ayuda;
    LinearLayout menu;
    static TextView nota;

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private View abrir_menu;
    private View dummy;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;

    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        context=this;
        funciones=new Funciones(context);
        titulogrupo=findViewById(R.id.titugrupo);
        abrir_menu=findViewById(R.id.abrir_menu);
        dummy=findViewById(R.id.dummy);
        menu=findViewById(R.id.menu);
        menu_salir=findViewById(R.id.menu_salir);
        menu_ayuda=findViewById(R.id.menu_ayuda);
        nota=findViewById(R.id.nota);
        SQR=funciones.GetIdGrupo();
        MobileAds.initialize(this);
        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        adRequest=new AdRequest.Builder().build();

        m.loadAd(adRequest);


        crear=findViewById(R.id.crear);
        unirse=findViewById(R.id.unirse);
        grupo_lay=findViewById(R.id.grupo_lay);
        botones=findViewById(R.id.botones);
        QR=findViewById(R.id.QR);

        abrir_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                MostrarMenu();
            }
        });

        menu_ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                startActivity(new Intent(context, AyudaGrupoTab.class));
            }
        });

        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcultarMenu();
            }
        });

        menu_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                Salir();
            }
        });



    }

    public void MostrarMenu(){

        dummy.setVisibility(View.VISIBLE);
        dummy.bringToFront();
        menu.setVisibility(View.VISIBLE);
        menu.bringToFront();
    }


    public void OcultarMenu(){

        menu.setVisibility(View.GONE);
        dummy.setVisibility(View.GONE);
    }

    public void OcultarItemsMenu(){
        findViewById(R.id.menu_salir).setVisibility(View.GONE);
        findViewById(R.id.menu_grupo).setVisibility(View.GONE);
    }

    public void MostrarItemsMenu(){
        findViewById(R.id.menu_salir).setVisibility(View.VISIBLE);
        findViewById(R.id.menu_grupo).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!funciones.IsGrupoEnviado() && SQR!=""){
            funciones.CorrerVerificarEnviado(nota);
        }
        OcultarMenu();

        if(SQR!=""){

            QuitarBotton();
            QR.setImageBitmap(funciones.GetQR(SQR));
            titulogrupo.setText(funciones.GetNombreGrupo());
            MostrarItemsMenu();
        }else{
            try {
                Intent intent = getIntent();
                Uri data = intent.getData();
                funciones.Logo("Getgrupo",data.toString());
                String[] array = data.toString().split("=");
                funciones.Logo("Getgrupo",array[1]);
                Guardar(array[1]);
                SQR=array[1];
            }catch (Exception e){
                Log.i("Getgrupo",e.getMessage().toString());
            }
            OcultarItemsMenu();
        }
        funciones.VerificarServicios();
    }

    public void Pop(View view) {
        funciones.Vibrar(funciones.VibrarPush());
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Grupo.this);
        alertDialog.setTitle("Crear Grupo");
        alertDialog.setMessage("Nombre del grupo?");

        final EditText nombre = new EditText(context);
        nombre.setHint("Nombre");
        nombre.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        nombre.setLayoutParams(lp);

        alertDialog.setView(nombre);

        alertDialog.setIcon(R.drawable.img_menu_grupo);

        alertDialog.setPositiveButton("Crear",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQR = funciones.GuardarGrupo(nombre.getText().toString());
                        QR.setImageBitmap(funciones.GetQR(SQR));
                        titulogrupo.setText(funciones.GetNombreGrupo());
                        QuitarBotton();
                        funciones.ForzarEnviador();
                        MostrarItemsMenu();
                        funciones.CorrerVerificarEnviado(nota);


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



    void QuitarBotton(){

        botones.setVisibility(View.GONE);
        grupo_lay.setVisibility(View.VISIBLE);
        findViewById(R.id.menu_salir).setVisibility(View.VISIBLE);
        findViewById(R.id.menu_grupo).setVisibility(View.VISIBLE);
        findViewById(R.id.menu_ayuda).setVisibility(View.VISIBLE);
    }



    public void Leer(View view){
        funciones.Vibrar(funciones.VibrarPush());
        if(PedirPermiso()) {
            escanear();
        }
    }


    public void escanear() {

        Intent i = new Intent(Grupo.this, Escaner.class);
        startActivityForResult(i, CODIGO_INTENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    funciones.Logo("Escaner2", codigo);
                    Guardar(codigo);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Escanear directamten solo si fue pedido desde el botón
                    if (permisoSolicitadoDesdeBoton) {
                        escanear();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    permisoDeCamaraDenegado();
                }
                break;
        }
    }




    private void permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(Grupo.this, "No puedes escanear si no das permiso", Toast.LENGTH_SHORT).show();
    }

    void Guardar(String codigo){
        if(codigo.length()==32){
            SQR=funciones.GuardarGrupo2(codigo);
            if(SQR!=""){
                QuitarBotton();
                QR.setImageBitmap(funciones.GetQR(SQR));
                titulogrupo.setText(funciones.GetNombreGrupo());
            }else{
                Toast.makeText(context, "Error al crear el grupo.", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(context, "Error en la lectura.", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean PedirPermiso() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.CAMERA};

            int camara = checkSelfPermission(Manifest.permission.CAMERA);

            if (camara == PackageManager.PERMISSION_GRANTED ) {

                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }else{
        }
        return true;
    }

    public void CompartirWats(View view){
        funciones.Vibrar(funciones.VibrarPush());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "www.app-lab.com.mx/Grupo.php?data="+SQR);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void CompartirFace(View view){
        Intent faceIntent = new Intent(Intent.ACTION_SEND);
        faceIntent.setType("text/plain");
        faceIntent.setPackage("com.facebook.orca");
        faceIntent.putExtra(Intent.EXTRA_TEXT, "www.app-lab.com.mx/Grupo.php?data="+SQR);
        try {
            startActivity(faceIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, Principal.class));
        finish();
    }

    public void VerListaVecinos(View view){
        startActivity(new Intent(this, ListaVecinos.class));
        //this.finish();
    }

    public void Salir(){
        funciones.Vibrar(funciones.VibrarPush());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Salir");
        alertDialog.setMessage("¿Abandonar grupo?");



        alertDialog.setIcon(R.drawable.img_menu_grupo);

        alertDialog.setPositiveButton("Salir",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        funciones.SalirGrupo();
                        funciones.VerificarServicios();
                        startActivity(new Intent(context,Grupo.class));
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

}
