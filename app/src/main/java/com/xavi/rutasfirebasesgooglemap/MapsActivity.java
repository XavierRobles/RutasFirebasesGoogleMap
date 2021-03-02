package com.xavi.rutasfirebasesgooglemap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AccesoFirebase.iRecuperarDatos {

    private GoogleMap mMap;
    private EditText nombre_ruta;
    private Spinner spn_rutas;
    private Button grabar_parar;
    private Button mostrar;
    private boolean estoy_grabando;
    private Location location;
    private LocationManager lm;
    private LocationListener ls;
    private ArrayList<Punto> puntos = new ArrayList<>();
    private PolylineOptions ruta = new PolylineOptions();
    private ArrayList<Ruta> rutas = new ArrayList<>();
    AccesoFirebase.iRecuperarDatos interfaz_recuperarDatos = this;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccesoFirebase.pedirRutasFirebase(interfaz_recuperarDatos);
        setContentView(R.layout.activitymain);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        inicializarCampos();
        ChekearPermiso();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void inicializarCampos() {
        nombre_ruta = findViewById(R.id.edt_input);
        grabar_parar = findViewById(R.id.bnt_grabar);
        mostrar = findViewById(R.id.btn_mostrar);
        spn_rutas = findViewById(R.id.spn_ruras);
        View.OnClickListener oyente_parar_grabar = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (estoy_grabando) {
                lm.removeUpdates(ls);
                grabarRutaFireBase();


                    grabar_parar.setText(R.string.grabar);
                } else {
                    ChekearPermiso();
                    grabar_parar.setText(R.string.parar);
                }
                estoy_grabando = !estoy_grabando;
            }
        };
        View.OnClickListener oyente_mostrar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            mostrarRutaSeleccionada();
            }
        };
        ls = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                 //Log.d("PUNTO", String.valueOf(location.getLatitude()));
                 Punto p = new Punto(location.getLatitude(), location.getLongitude());
                 puntos.add(p);

                 insertarPunto(p);
            }
        };
        grabar_parar.setOnClickListener(oyente_parar_grabar);
        mostrar.setOnClickListener(oyente_mostrar);
    }

    private void mostrarRutaSeleccionada() {
        mMap.clear();
        ruta = new PolylineOptions();

        Ruta ruta_seleccionada = (Ruta) spn_rutas.getSelectedItem();
        ArrayList<Punto> lista_puntos= ruta_seleccionada.getLista_puntos();
        for (Punto p: lista_puntos)
            insertarPunto(p);
    }

    private void insertarPunto(Punto p) {
        LatLng x = new LatLng(p.getLat(), p.getLng());
        ruta.add(x);
        mMap.addPolyline(ruta);
    }

    public void grabarRutaFireBase(){
        String ruta_nombre = nombre_ruta.getText().toString();
        Ruta r = new Ruta(ruta_nombre, puntos);
        AccesoFirebase.grabarRuta(r);


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Solicita permiso para usar el GPS
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ChekearPermiso() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //si no tengo permiso lo pido
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //si ya lo tengo ejecuto.
        pedirActualizaciones();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //pediActualizacion directamente
                pedirActualizaciones();
            }
        }
    }

    public void pedirActualizaciones() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, ls);
    }


    @Override
    public void recuperarRutas(ArrayList<Ruta> lista_rutas) {
        rutas = lista_rutas;
        rellenarSpiner();
        Log.d("DESDE MAIN", lista_rutas.toString());
    }

    private void rellenarSpiner() {
        ArrayAdapter<Ruta>  adaptador_rutas = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, rutas);
        spn_rutas.setAdapter(adaptador_rutas);
    }
}