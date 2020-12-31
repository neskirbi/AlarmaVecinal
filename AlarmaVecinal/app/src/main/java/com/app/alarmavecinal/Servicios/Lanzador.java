package com.app.alarmavecinal.Servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.app.alarmavecinal.Funciones;

public class Lanzador extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Funciones funciones=new Funciones(context);
        if(funciones.GetIdGrupo().replace(" ","").length()==32) {
            if (!funciones.isMyServiceRunning(Emergencia.class, context)) {
                Intent service1 = new Intent(context, Emergencia.class);
                service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(service1);
                }
                context.startService(service1);
            }


            if (!funciones.isMyServiceRunning(Notificador.class, context) && funciones.Check_Log()) {
                Intent service3 = new Intent(context, Notificador.class);
                service3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(service3);
                }
                context.startService(service3);


            }
        }
        if (!funciones.isMyServiceRunning(Enviador.class, context) && funciones.Check_Log()) {
            Intent service2 = new Intent(context, Enviador.class);
            service2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service2);
            }
            context.startService(service2);


        }


    }



}
