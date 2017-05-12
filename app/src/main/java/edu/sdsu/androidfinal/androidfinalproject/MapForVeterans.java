package edu.sdsu.androidfinal.androidfinalproject;

import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapForVeterans extends Fragment implements OnMapReadyCallback {

    MapView setCityMapView;
    private GoogleMap regularMap;
    private LatLng reportLocation;
    public Button report;

    Geocoder locateAddress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.map_for_regular_user, container, false);

        setCityMapView = (MapView) view.findViewById(R.id.mapView);
        setCityMapView.onCreate(savedInstanceState);

        setCityMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        report = (Button) view.findViewById(R.id.reportHomeless);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get location
            }
        });

        setCityMapView.getMapAsync(this);

        return view;
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        regularMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        regularMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        regularMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
