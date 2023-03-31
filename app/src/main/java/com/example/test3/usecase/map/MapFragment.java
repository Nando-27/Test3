package com.example.test3.usecase.map;

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
import com.example.test3.R;
import com.example.test3.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private FragmentMapBinding binding;

    GoogleMap nMap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        supportMapFragment.getMapAsync(this);

        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marcador(latLng);
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
        LatLng marcador = new LatLng(latLng.latitude, latLng.longitude);
        nMap.addMarker(new MarkerOptions().position(marcador).title(" Latitud: "+latLng.latitude+" Longitud: "+latLng.longitude));

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