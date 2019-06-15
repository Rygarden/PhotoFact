package com.diploma.nurzhan.photo_fact.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.diploma.nurzhan.photo_fact.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.diploma.nurzhan.photo_fact.ui.ConfirmActivity.mapLat;
import static com.diploma.nurzhan.photo_fact.ui.ConfirmActivity.mapLon;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.mLat;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.mLon;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    public Double newLocationLat;
    public Double newLocationLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Google Map");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        MapView mapView = findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        finish();
//        return super.onSupportNavigateUp();
//    }

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
        Log.d(TAG, "onMapReady: called.");
        
        mMap = googleMap;
        final Marker[] marker = new Marker[1];


        final Marker marker1;
        // Add a marker in Sydney and move the camera
        final LatLng myLocation = new LatLng(mLat, mLon);
        marker1 = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((myLocation), 15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub

                if (marker1 != null) {
                    marker1.remove();
                }
                marker[0] = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("ME"));
                Log.e("lat", "" + point);



//                newLocation = point;

                Double l1=point.latitude;
                Double l2=point.longitude;
                newLocationLat = l1;
                newLocationLon = l2;

            }
        });



//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng latlng) {
//                Log.d(TAG, "onMapClick: called.");
//                // TODO Auto-generated method stub
//
//                if (marker[0] != null) {
//                    marker[0].remove();
//                }
//                marker[0] = mMap.addMarker(new MarkerOptions()
//                        .position(latlng)
//                        .icon(BitmapDescriptorFactory
//                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                Log.d(TAG, "onMapClick: latlng: " + latlng);
//
//            }
//        });

    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        mapLat = newLocationLat;
        mapLon = newLocationLon;

    }
}
