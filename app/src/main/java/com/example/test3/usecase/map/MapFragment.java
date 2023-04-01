package com.example.test3.usecase.map;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test3.R;
import com.example.test3.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private FragmentMapBinding binding;
    private TextView coordenadas;
    private Button btnguardar;
    private FirebaseFirestore db;

    private String Latitud, Longitud;

    GoogleMap nMap;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        supportMapFragment.getMapAsync(this);

        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);


        Obtenerdatos();

        return root;

    }

    private void setup() {
        coordenadas = (TextView) getView().findViewById(R.id.txtcoordenadas);
        btnguardar = (Button) getView().findViewById(R.id.btnguardar);
        Latitud = "";
        Longitud = "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void guardardatos() {
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cordenadas = coordenadas.getText().toString().trim();
                if (cordenadas.isEmpty()){
                    Toast.makeText(getActivity(),"Selecione una ubicacion", Toast.LENGTH_SHORT).show();
                }else {
                    guardarDatosbd(cordenadas);
                }
            }
        });
    }

    private void guardarDatosbd(String cordenadas) {

        Map<String, Object> bdtest= new HashMap<>();
        bdtest.put("coordenadas",cordenadas);
        bdtest.put("Latitud",Latitud);
        bdtest.put("Longitud",Longitud);

        db.collection("BDTest")
                .document(cordenadas.toString())
                .set(bdtest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(),"Ubicacion Guardada", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void Obtenerdatos(){
        db.collection("BDTest")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(getActivity(),"Actualizando", Toast.LENGTH_SHORT).show();
                                LatLng marcador = new LatLng(
                                        Double.valueOf(document.getString("Latitud")),
                                        Double.valueOf(document.getString("Longitud")));

                                nMap.addMarker(new MarkerOptions().position(marcador).title(" Latitud: "+document.getString("Latitud")+" Longitud: "+document.getString("Longitud")));

                            }
                        } else {
                            Toast.makeText(getActivity(),"Error al cargar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        nMap = googleMap;

        nMap.setMinZoomPreference(5);
        nMap.setMaxZoomPreference(16);
        getLocalizacionON();
        Mizona();
        this.nMap.setOnMapClickListener(this);
        this.nMap.setOnMapLongClickListener(this);
    }
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Marcador(latLng);
        guardardatos();
    }



    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marcador(latLng);
        guardardatos();
    }



    private void Mizona() {
        LatLng  zona = new LatLng(-8.1,-79.0);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(zona)
                .zoom(10)
                .tilt(45)
                .build();
        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void Marcador(LatLng latLng) {

        nMap.clear();
        Obtenerdatos();
        LatLng marcador = new LatLng(latLng.latitude, latLng.longitude);
        nMap.addMarker(new MarkerOptions().position(marcador).title(" Latitud: "+latLng.latitude+" Longitud: "+latLng.longitude));
        coordenadas.setText(""+marcador.latitude+" "+marcador.longitude);
        Latitud = String.valueOf(marcador.latitude);
        Longitud = String.valueOf(marcador.longitude);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marcador)
                .zoom(16)
                .tilt(45)
                .build();
        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getLocalizacionON(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            nMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {

                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Integer.parseInt(LOCATION_SERVICE));
            }
        }
    }
}