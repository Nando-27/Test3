package com.example.test3.usecase.map;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test3.R;
import com.example.test3.databinding.FragmentMapBinding;
import com.example.test3.model.MapModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    private FragmentMapBinding binding;
    private TextView coordenadas;
    private FirebaseFirestore db;

    private MapModel mapModel;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Dialog registrarModal;
    private Spinner sptipoincidencia;
    MapViewModel mapViewModel;
    GoogleMap nMap;

    TextView tHoras,tFecha,tlugar;

    int hora, minutos;
    Calendar c;
    Geocoder geocoder;

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

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        geocoder = new Geocoder(getActivity());


        return root;
    }

    private void setup() {
        DetalleSetup();
        registrarModalSetup();

    }

    private void registrarModalSetup() {
        registrarModal = new Dialog(getActivity());
        registrarModal.requestWindowFeature(Window.FEATURE_NO_TITLE);
        registrarModal.setCancelable(true);
        registrarModal.setContentView(R.layout.dialog_registrar);
    }

    private void RegistrarDetalle(String cordenadas, String tipo,String lugar,String fecha, String hora,double latitud,double longitud) {


        TextView coordenadasR = registrarModal.findViewById(R.id.txtCoordenadasRegistrar);
        TextView lugarr = registrarModal.findViewById(R.id.txtLugarr);
        TextView Tipo = registrarModal.findViewById(R.id.txtTipo);
        TextView horar=registrarModal.findViewById(R.id.txtrHora);
        TextView fechar=registrarModal.findViewById(R.id.txtrfecha);
        EditText description = registrarModal.findViewById(R.id.txtDescripcion);
        Button btnguardarr= registrarModal.findViewById(R.id.btnguardarr);


        coordenadasR.setText("Coordenadas: "+cordenadas);
        lugarr.setText("Lugar: "+lugar);
        Tipo.setText("Tipo de Incidencia: "+tipo);
        horar.setText("Hora :"+hora);
        fechar.setText("Fecha: "+fecha);


        registrarModal.show();
    }



    private void DetalleSetup() {
        bottomSheetDialog = new BottomSheetDialog(
                getActivity(), R.style.BotomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getActivity().getApplicationContext())
                .inflate(
                        R.layout.layout_botomsheet,
                        (LinearLayout) getView().findViewById(R.id.bottomsheetcontainer)
                );


        coordenadas = bottomSheetView.findViewById(R.id.txtcoordenadasbt);

        ArrayAdapter<String> tiposi = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_layout_items);
        tiposi.addAll("Robo","Asalto");

        sptipoincidencia = bottomSheetView.findViewById(R.id.spTiposIncidencias);
        sptipoincidencia.setAdapter(tiposi);

        tHoras = bottomSheetView.findViewById(R.id.txtHora);
        tFecha = bottomSheetView.findViewById(R.id.txtFecha);
        tlugar = bottomSheetView.findViewById(R.id.txtLugar);

        ConfigurarHora();
        ConfigurarFecha();

    }

    private void ConfigurarFecha() {

        tFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c = Calendar.getInstance();
                int dia = c.get(Calendar.DAY_OF_MONTH);
                int mes = c.get(Calendar.MONTH);
                int anio = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int manio, int mmes, int mdia) {
                        tFecha.setText(mdia+"/"+(mmes+1)+"/"+manio);
                    }
                },dia,mes,anio);
                datePickerDialog.show();
            }
        });



    }

    private void ResetearFecha() {
        Date date = new Date(System.currentTimeMillis());
        tFecha.setText((new SimpleDateFormat("dd/MM/yy")).format(date));
    }

    private void ConfigurarHora() {
        ResetearHora();
        tHoras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hora = hourOfDay;
                                minutos = minute;

                                String time = hora + ":" + minute;

                                SimpleDateFormat f24hour = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = f24hour.parse(time);
                                    SimpleDateFormat f12hour = new SimpleDateFormat("HH:mm aa");

                                    tHoras.setText(f12hour.format(date));
                                }catch (ParseException e){
                                    e.printStackTrace();
                                }

                            }
                        },12,0,false


                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hora, minutos);
                timePickerDialog.show();
            }
        });
    }

    private void ResetearHora() {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm aa");
        String Formatte = df.format(dt.getTime());
        tHoras.setText(Formatte);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        nMap = googleMap;

        nMap.setMinZoomPreference(9);
        nMap.setMaxZoomPreference(16);

        this.nMap.setOnMapClickListener(this);
        this.nMap.setOnMapLongClickListener(this);

        getLocalizacionON();
        ModoOscuro();
        LimitarArea();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Marcador(latLng);

    }


    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marcador(latLng);
    }

    private void Marcador(LatLng latLng) {

        nMap.clear();

        LatLng marcador = new LatLng(latLng.latitude, latLng.longitude);
        nMap.addMarker(new MarkerOptions().position(marcador).title(marcador.latitude+" "+marcador.longitude));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marcador)
                .zoom(15)
                .build();
        nMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        coordenadas.setText(marcador.latitude+" "+marcador.longitude);

        DetallesUbicacion(marcador.latitude,marcador.longitude);
    }

    private void LimitarArea() {
        LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(-8.192294, -79.174916), // SW bounds
                new LatLng(-8.013480, -78.858350)  // NE bounds
        );

        nMap.setLatLngBoundsForCameraTarget(adelaideBounds);
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

    private void guardarDatosbd() {
        Map<String, Object> bdtest =new HashMap<>();
        MapModel mapModel1 = new MapModel();



        db.collection("BDTest")
                .document()
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
           ObtnerMarcadores();



    }
    private void ObtnerMarcadores() {
        db.collection("BDTest")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

/*
                                if (!document.getId().isEmpty()){
                                    Toast.makeText(getActivity(),"No hay Incidencias", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getActivity(),"No hay Incidencias", Toast.LENGTH_SHORT).show();
                                }

                                double lat = Double.parseDouble(document.getString("Latitud"));
                                double lng = Double.parseDouble(document.getString("Longitud"));

                                LatLng marcador = new LatLng(lat,lng);

                                nMap.addMarker(new MarkerOptions()
                                        .position(marcador)
                                        .title(marcador.toString()));
                                costructor.include(marcador);*/

                            }
                            /*nMap.clear();
                            LatLngBounds.Builder costructor = new LatLngBounds.Builder();
                            LatLngBounds limite = costructor.build();

                            int ancho = getResources().getDisplayMetrics().widthPixels;
                            int alto = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (alto * 0.10);

                            CameraUpdate centrar = CameraUpdateFactory.newLatLngBounds(limite,ancho,alto,padding);
                            nMap.animateCamera(centrar);
                            LimitarArea();*/

                        } else {
                            Toast.makeText(getActivity(),"Error al cargar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void ModoOscuro() {
        try {

            boolean success = nMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }


    private void DetallesUbicacion(double latitud, double longitud) {

        Button btnguardar = bottomSheetView.findViewById(R.id.btnguardarbt);
        Button btncancelar = bottomSheetView.findViewById(R.id.btnCancelar);

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistrarDetalle(
                        coordenadas.getText().toString(),
                        sptipoincidencia.getSelectedItem().toString(),
                        tFecha.getText().toString(),
                        tHoras.getText().toString(),
                        tlugar.getText().toString(),
                        latitud,longitud

                );
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Obtenerdatos();
                nMap.clear();
                ResetearHora();

            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
    }



}