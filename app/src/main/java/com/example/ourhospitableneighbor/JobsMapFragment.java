package com.example.ourhospitableneighbor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ourhospitableneighbor.model.Job;
import com.example.ourhospitableneighbor.view.PanelView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.TimeUnit;

public class JobsMapFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private PanelView panel;

    private Debouncer showJobsDebouncer = new Debouncer();
    private FusedLocationProviderClient locationClient;

    private boolean loadingJobsFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jobs_map, container, false);
        panel = rootView.findViewById(R.id.panel);
        locationClient = LocationServices.getFusedLocationProviderClient(getContext());
        addMapFragment();
        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Reload the map so my location button works
                addMapFragment();
            }
        }
    }

    private void onMapReady(GoogleMap map) {
        this.map = map;

        Context ctx = this.getActivity();
        assert ctx != null;

        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                JobService.getInstance().setUserCurrentLocation(location);
                showJobsInAreaDebounced();
            });
            setUpMyLocationButton();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        map.setOnCameraIdleListener(this::showJobsInAreaDebounced);
        map.getUiSettings().setMapToolbarEnabled(false);

        setUpCompassButton();
        setInitialViewPoint();
        showAllJobMarkers();
    }

    private void setInitialViewPoint() {
        // Somewhere in New West
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(49.2156, -122.9177), 14);
        map.moveCamera(update);
    }

    private void addMapFragment() {
        mapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
        mapFragment.getMapAsync(this::onMapReady);
    }

    @SuppressLint("MissingPermission")
    private void setUpMyLocationButton() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        View mapView = mapFragment.getView();
        if (mapView == null) return;

        View locationButton = mapView.findViewWithTag("GoogleMapMyLocationButton");
        if (locationButton == null) return;

        // position on right bottom
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 30);
    }

    private void setUpCompassButton() {
        View mapView = mapFragment.getView();
        if (mapView == null) return;

        View compassButton = mapView.findViewWithTag("GoogleMapCompass");
        if (compassButton == null) return;

        // position on left bottom
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 30);
    }

    private void showJobsInAreaDebounced() {
        showJobsDebouncer.debounce(this::showJobsInArea, 200, TimeUnit.MILLISECONDS);
    }

    @SuppressLint("MissingPermission")
    private void showJobsInArea() {
        getActivity().runOnUiThread(() -> {
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
            AsyncTask.execute(() -> {
                JobService.getInstance().getJobsInArea(bounds).addOnSuccessListener(jobs -> {
                    panel.setJobs(jobs);
                    if (loadingJobsFirstTime) {
                        panel.setCollapse(false, true);
                        loadingJobsFirstTime = false;
                    }
                });
            });
        });
    }

    private void showAllJobMarkers() {
        JobService.getInstance().getAllJobs().addOnSuccessListener(jobs -> {
            map.clear();
            for (Job job : jobs) {
                map.addMarker(createMarkerFromJob(job));
            }
        });
    }

    private MarkerOptions createMarkerFromJob(Job job) {
        return new MarkerOptions()
                .position(new LatLng(job.getLatitude(), job.getLongitude()))
                .title(job.getJobTitle());
    }
}