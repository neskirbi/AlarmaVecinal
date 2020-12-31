package com.app.alarmavecinal.Servicios;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Enviador extends Service {
    Boolean bandera=null;
    Funciones funciones;
    Context context;
    public Enviador() {
        context=this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bandera=true;

        funciones=new Funciones(this);

        @SuppressLint("StaticFieldLeak") AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected void onProgressUpdate(Object[] values) {

            }

            @Override
            protected Object doInBackground(Object[] objects) {
                while (bandera){

                    funciones.EnviarGrupo();
                    //funciones.SetChat();
                    funciones.CheckGrupo();

                    Thread tr=new Thread();
                    try {
                        tr.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }
        };

        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        bandera=false;

        super.onDestroy();
    }


}
