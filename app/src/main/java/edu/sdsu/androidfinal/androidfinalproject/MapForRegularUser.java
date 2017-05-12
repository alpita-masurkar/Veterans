package edu.sdsu.androidfinal.androidfinalproject;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//public class MapForRegularUser extends Fragment implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener {
public class MapForRegularUser extends Fragment implements OnMapReadyCallback,
    LocationListener {

    MapView setCityMapView;
    private GoogleMap regularMap;
    GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private LatLng reportLocation;
    public Button report;

    Geocoder locateAddress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.map_for_regular_user, container, false);

        checkLocationPermission();

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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Asking user if explanation is needed
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //Prompt the user once explanation has been shown
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 6);
        regularMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

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
        regularMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
         //       buildGoogleApiClient();
                regularMap.setMyLocationEnabled(true);
            }
        }
        else {
           // buildGoogleApiClient();
            regularMap.setMyLocationEnabled(true);
        }

        regularMap.getUiSettings().setCompassEnabled(false);
        regularMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        regularMap.getUiSettings().setMapToolbarEnabled(false);
        regularMap.getUiSettings().setRotateGesturesEnabled(false);
        regularMap.getUiSettings().setZoomControlsEnabled(true);

//        regularMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 8);
//                regularMap.animateCamera(cu);
//            }
//        });

        regularMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng finalLatLng) {
                reportLocation = finalLatLng;
                regularMap.clear();
                System.out.println("City selected: " + reportLocation.toString());
                regularMap.addMarker(new MarkerOptions().position(reportLocation).title("Fill Info"));
                regularMap.moveCamera(CameraUpdateFactory.newLatLng(reportLocation));
            }
        });
    }

//    protected synchronized void buildGoogleApiClient() {
//        googleApiClient = new GoogleApiClient.Builder(getContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        googleApiClient.connect();
//    }
}
