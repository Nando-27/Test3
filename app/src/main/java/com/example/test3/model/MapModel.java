package com.example.test3.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapModel{

    private String Coordenadas;
    private String Latitud;
    private String Longitud;

    public MapModel() {
    }

    public MapModel(String coordenadas, String latitud, String longitud) {
        Coordenadas = coordenadas;
        Latitud = latitud;
        Longitud = longitud;
    }

    public String getCoordenadas() {
        return Coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        Coordenadas = coordenadas;
    }

    public String getLatitud() {
        return Latitud;
    }

    public void setLatitud(String latitud) {
        Latitud = latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public void setLongitud(String longitud) {
        Longitud = longitud;
    }

    @Override
    public String toString() {
        return  Coordenadas.toString() ;
    }
}