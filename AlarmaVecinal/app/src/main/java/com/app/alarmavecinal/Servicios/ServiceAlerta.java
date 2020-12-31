package com.app.alarmavecinal.Servicios;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;

import com.app.alarmavecinal.FuncionAlertas.AlertasLista;
import com.app.alarmavecinal.FuncioneAvisos.AvisosLista;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Chat.ChatWindow;
import com.app.alarmavecinal.VerEmergencia;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceAlerta extends Service {
    Boolean bandera=null;
    Funciones funciones;
    Context context;
    AudioManager audioManager;
    MediaPlayer alarma;
    public ServiceAlerta() {
        context=this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bandera=true;
        funciones=new Funciones(context);
        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alarma = MediaPlayer.create(context, R.raw.alertaroja);

        @SuppressLint("StaticFieldLeak") AsyncTask asyncTask=new AsyncTask() {
            @Override
            protected void onProgressUpdate(Object[] values) {
                funciones.Logo("alertando",values[0]+"");
                String mensaje="";
                try {
                    JSONObject jsonObject=new JSONObject(values[0]+"");
                    if(jsonObject.has("id_emergencia")){
                        String nombre=jsonObject.getString("nombres")+" "+jsonObject.getString("apellidos"),direccion=jsonObject.getString("direccion");
                        mensaje="Notificado por : "+jsonObject.getString("nombres")+" "+jsonObject.getString("apellidos")+" \n "+jsonObject.getString("direccion");
                        //bit= BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.logo);
                        funciones.Notificar("¡Emergencia!",mensaje,R.drawable.logo,new Intent(context, VerEmergencia.class).putExtra("nombre",nombre).putExtra("direccion",direccion),0);

                        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int currentVolumePercentage = 100 * currentVolume/maxVolume;

                        funciones.Logo("volumen",currentVolume+"----"+maxVolume+"------"+currentVolumePercentage);
                        //AudioManager mobilemode = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
                        if(maxVolume>currentVolume){
                            if(!BuildConfig.DEBUG){
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
                            }

                        }

                        if (!alarma.isPlaying()){
                            alarma.start();
                            funciones.Logo("volumen","corriendo");
                        }


                    }

                    if(jsonObject.has("id_alerta")){
                        if(funciones.GetAlertas(values[0]+"")){
                            mensaje=jsonObject.getString("asunto");
                            funciones.Notificar("¡Alerta!",mensaje,R.drawable.img_menu_alerta,new Intent(context, AlertasLista.class),1);
                        }

                    }

                    if(jsonObject.has("id_aviso")){
                        if(funciones.GetAvisos(values[0]+"")){
                            mensaje=funciones.ToString(jsonObject.getString("asunto"))+" \n "+funciones.ToString(jsonObject.getString("mensaje"));
                            funciones.Notificar("¡Aviso!",mensaje,R.drawable.img_menu_aviso,new Intent(context, AvisosLista.class),2);
                        }

                    }

                    if(jsonObject.has("id_mensaje")){
                        funciones.Notificar("¡Nuevo mensaje!","¡Hay nuevos mensajes en el chat!",R.drawable.sobre,new Intent(context, ChatWindow.class),3);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    funciones.Logo("Error","error"+e.getMessage());
                }


                super.onProgressUpdate(values);
            }

            @Override
            protected Object doInBackground(Object[] objects) {

                while (bandera){
                    try{
                        /*if(funciones.GetChat()){

                            publishProgress("{\"id_mensaje\":\"algo\"}");
                        }*/
                        funciones.UpdatefbToken();
                    }catch (Exception e){
                        funciones.Logo("topActivity", "Error ::" + e.getMessage());
                    }

                    String response="",id_grupo="";
                    id_grupo=funciones.GetIdGrupo();
                    if(id_grupo!=""){

                        try {
                            response=funciones.Conexion("{\"id_grupo\":\""+id_grupo+"\"}",funciones.GetUrl()+getString(R.string.url_GetNotificaciones));
                            JSONArray jsonArray =new JSONArray(response);
                            if(jsonArray.length()!=0){
                               for(int i=0;i<jsonArray.length();i++){
                                   publishProgress(jsonArray.get(i));
                               }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            funciones.Logo("conexion", e.getMessage());
                        }
                    }



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
