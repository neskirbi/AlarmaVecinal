package com.app.alarmavecinal.Mapas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.alarmavecinal.Adapters.AdapterEmergencias;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MapaEmergencia extends AppCompatActivity implements OnMapReadyCallback, AdapterEmergencias.RecyclerItemClick {

    TextView tag_nombre;
    Funciones funciones = null;
    Context context;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    RecyclerView emergencias_lista;
    ArrayList<Emergencias> emergencias = new ArrayList<>();
    private MapaEmergencia algo;
    private GoogleMap map;
    double verlat = 0.0, verlon = 0.0;
    String vernombre = "", verdireccion = "";

    FloatingActionButton verruta;
    LinearLayout contenedor_verruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_emergencia);
        setTitle("Emergencia");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        algo = this;
        funciones = new Funciones(context);
        emergencias_lista = findViewById(R.id.emergencias_lista);
        contenedor_verruta = findViewById(R.id.contenedor_verruta);
        verruta = findViewById(R.id.verruta);
        tag_nombre = findViewById(R.id.tag_nombre);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


        MobileAds.initialize(this);
        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);


        verruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                if (verlat != 0.0 || verlon != 0.0) {
                    PedirPermisoLocation();
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (funciones.GetUbicacion().replace(" ", "").length() > 0) {
            JSONObject coordenadas = null;
            try {
                coordenadas = new JSONObject(funciones.GetUbicacion());
                Mover(Double.parseDouble(coordenadas.getString("lat")), Double.parseDouble(coordenadas.getString("lon")), 10);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        // Check if we were successful in obtaining the map.
        if (googleMap != null) {
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {


                }
            });
        }


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EmergenciaVecinos-"+funciones.GetIdGrupo());//Sala de chat

        listener=new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Emergencia", snapshot.getValue()+"");
                Emergencias emergencia=snapshot.getValue(Emergencias.class);
                emergencias.add(new Emergencias(emergencia.getId_emergencia(),emergencia.getId_usuario(),emergencia.getNombre(),emergencia.getMensaje(),emergencia.getUbicacion(),emergencia.getFecha() ));
                //Marcando una por una
                JSONObject ubicaciont= null;
                try {
                    ubicaciont = new JSONObject(emergencia.getUbicacion());
                    String[] coordenadas=ubicaciont.getString("ubicacion").split(",");
                    Marcar(emergencia.getNombre(),
                            Double.parseDouble(coordenadas[0]),
                            Double.parseDouble((coordenadas[1])));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                Collections.reverse(emergencias);
                emergencias_lista.setLayoutManager(new LinearLayoutManager(context));
                emergencias_lista.setAdapter(new AdapterEmergencias(emergencias,algo));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addChildEventListener(listener);



        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,16f));

    }

    public Boolean PedirPermisoLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

            int location = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (location == PackageManager.PERMISSION_GRANTED ) {
                startActivity(new Intent(context,Ruta.class).putExtra("lat",verlat).putExtra("lon",verlon).putExtra("nombre",vernombre).putExtra("direccion",verdireccion));
                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    public void itemClick(Emergencias emergencias, int position) {
        tag_nombre.setText(emergencias.getNombre());
        funciones.Vibrar(funciones.VibrarPush());
        contenedor_verruta.setVisibility(View.VISIBLE);

        try {
            JSONObject ubicaciont=new JSONObject(emergencias.getUbicacion());
            String[] coordenadas=ubicaciont.getString("ubicacion").split(",");
            Mover( Double.parseDouble(coordenadas[0]),Double.parseDouble((coordenadas[1])),16);
            vernombre=emergencias.getNombre();
            verdireccion=ubicaciont.getString("direccion");
            verlat=Double.parseDouble(coordenadas[0]);
            verlon=Double.parseDouble(coordenadas[1]);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("coordenadas2","Error:" +e.getMessage());
        }


    }

    public void Marcar(String nombre, double lat,double lon){
        Log.i("coordenadas2","lat:"+lat+"    lon:"+lon);
        LatLng coordenadas = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(coordenadas).title(nombre)).showInfoWindow();
    }
    public void Mover(double lat,double lon,float altura){
        LatLng coordenadas = new LatLng(lat, lon);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,altura));
    }

}