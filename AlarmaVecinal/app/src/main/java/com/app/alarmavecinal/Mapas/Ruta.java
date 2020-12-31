package com.app.alarmavecinal.Mapas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.DirectionsJSONParser;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ruta extends AppCompatActivity implements OnMapReadyCallback {
    String direccion, nombre;
    double lat, lon,latabs,lonabs;
    ArrayList<Double> latl=new ArrayList<>(), lonl=new ArrayList<>();
    LocationManager locationManager = null;
    LocationListener locationListener = null;
    int cont=0;
    GoogleMap googleMap;
    private Polyline mPolyline;


    private SensorManager sensorManager;
    private Sensor sensor;
    SensorEventListener mSensorEventListener=null;
    private final float[] magnetometerReading = new float[3];
    private double angulo;
    TextView contenedor_direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        contenedor_direccion=findViewById(R.id.contenedor_direccion);


        nombre = getIntent().getExtras().get("nombre").toString();
        direccion = getIntent().getExtras().get("direccion").toString();
        lat = Double.parseDouble(getIntent().getExtras().get("lat").toString());
        lon = Double.parseDouble(getIntent().getExtras().get("lon").toString());
        setTitle(nombre);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        contenedor_direccion.setText("Direccion: "+direccion);

        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        //iniciando sensor rotacion
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    float X_Axis = event.values[0];
                    float Y_Axis = event.values[1];
                    angulo = Math.atan2(X_Axis, Y_Axis)/(Math.PI/180)*-1;


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);

        MobileAds.initialize(this);
        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMapt) {



        googleMap = googleMapt;
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(nombre));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{

            googleMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        latabs=arg0.getLatitude();
                        lonabs=arg0.getLongitude();

                        CamaraPosition();

                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,18));

                        if(cont==0) {
                            latl.add(arg0.getLatitude());
                            lonl.add(arg0.getLongitude());
                            Trazar();
                            cont++;
                        }

                    }
                });
            }
        }

    }


    public void CamaraPosition(){

        if(latabs!=0.0 && lonabs!=0.0 ){
            LatLng ubicacion = new LatLng(latabs, lonabs);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(19)                   // Sets the zoom
                    .bearing((float) angulo)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }



    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.d("Background Task",""+url);
        return url;
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    List<List<HashMap<String, String>>> result1;
    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;

            Log.i("errorasynk",""+jsonData[0]);
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                result1 = parser.parse(jObject);
            }catch(Exception e){
                Log.i("errorasynk","error: "+e.getMessage());
            }
            return result1;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result1.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result1.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(20);
                lineOptions.color(R.color.polyline);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = googleMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
/*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        } else {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    latl.add(location.getLatitude());
                    lonl.add(location.getLongitude());
                    Parar();
                    cont++;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
        if (locationListener != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 100, locationListener);
        }*/
    }

    private void Trazar() {
        if(cont==0){
            String url = getDirectionsUrl(new LatLng(latl.get(0), lonl.get(0)),new LatLng(lat, lon));

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            LatLng ubicacion = new LatLng(latl.get(0), lonl.get(0));
            //googleMap.addMarker(new MarkerOptions().position(ubicacion) .title(direccion));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,18f));

        }

        if (locationListener != null && locationListener!=null)
            locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

//https://maps.googleapis.com/maps/api/directions/json?origin=19.49202381,-99.0957779&destination=19.494023809,-99.0937779&key=AIzaSyD1UpmBYBueQcmF_zwJ_iakT0CUXpyyDlQ
//https://maps.googleapis.com/maps/api/directions/json?origin=19.49202755,-99.09577831&destination=19.49402755,-99.09377831&key=AIzaSyD1UpmBYBueQcmF_zwJ_iakT0CUXpyyDlQ