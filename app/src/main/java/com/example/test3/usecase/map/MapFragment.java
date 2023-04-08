package com.example.test3.usecase.map;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test3.R;
import com.example.test3.databinding.FragmentMapBinding;
import com.example.test3.model.MapModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private FragmentMapBinding binding;
    private TextView coordenadas;
    private FirebaseFirestore db;

    private ClusterManager<MapModel> clusterManager;

    private String Latitud, Longitud;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;

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
        Latitud = "";
        Longitud = "";

        bottomSheetDialog = new BottomSheetDialog(
                getActivity(), R.style.BotomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getActivity().getApplicationContext())
                .inflate(
                        R.layout.layout_botomsheet,
                        (LinearLayout) getView().findViewById(R.id.bottomsheetcontainer)
                );


        coordenadas = bottomSheetView.findViewById(R.id.txtcoordenadasbt);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                                nMap.clear();
                                LatLngBounds.Builder costructor = new LatLngBounds.Builder();
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    double lat = Double.parseDouble(document.getString("Latitud"));
                                    double lng = Double.parseDouble(document.getString("Longitud"));
                                    Toast.makeText(getActivity(),"Actualizando", Toast.LENGTH_SHORT).show();
                                    LatLng marcador = new LatLng(lat,lng);

                                    nMap.addMarker(new MarkerOptions()
                                            .position(marcador)
                                            .title(marcador.toString()));
                                    costructor.include(marcador);

                                }

                                    LatLngBounds limite = costructor.build();

                                    int ancho = getResources().getDisplayMetrics().widthPixels;
                                    int alto = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (alto * 0.10);

                                    CameraUpdate centrar = CameraUpdateFactory.newLatLngBounds(limite,ancho,alto,padding);
                                    nMap.animateCamera(centrar);

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
        this.nMap.setOnMapClickListener(this);
        this.nMap.setOnMapLongClickListener(this);
    }
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Marcador(latLng);

    }




    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marcador(latLng);
    }

    private void DetallesUbicacion() {

        Button btnguardar = bottomSheetView.findViewById(R.id.btnguardarbt);
        Button btncancelar = bottomSheetView.findViewById(R.id.btnCancelar);
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatosbd(coordenadas.getText().toString());
                Obtenerdatos();
                bottomSheetDialog.dismiss();
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Obtenerdatos();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
    }

    private void Marcador(LatLng latLng) {

        nMap.clear();
        LatLng marcador = new LatLng(latLng.latitude, latLng.longitude);

        Latitud = String.valueOf(marcador.latitude);
        Longitud = String.valueOf(marcador.longitude);
        coordenadas.setText("Latitud: "+Latitud+" Longitud: "+Longitud);

        nMap.addMarker(new MarkerOptions().position(marcador).title(" Latitud: "+Latitud+" Longitud: "+Longitud));



        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marcador)
                .zoom(15)
                .build();
        nMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        DetallesUbicacion();
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