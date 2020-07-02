package com.example.ourhospitableneighbor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ourhospitableneighbor.model.JobInterface;
import com.example.ourhospitableneighbor.model.MockJob;
import com.example.ourhospitableneighbor.view.PanelView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class JobsMapFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private PanelView panel;
    private ViewGroup panelHeader;
    private PanelInteractionHandler panelInteractionHandler = new PanelInteractionHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jobs_map, container, false);
        addMapFragment();
        configurePanel(rootView);
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
            setUpMyLocationButton();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        setUpCompassButton();
        setInitialViewPoint();
        map.getUiSettings().setMapToolbarEnabled(false);
        showJobs();
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
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);

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

    private void showJobs() {
        List<JobInterface> jobs = MockJob.getJobs();
        for (JobInterface job : jobs) {
            map.addMarker(createMarkerFromJob(job));
        }
    }

    private MarkerOptions createMarkerFromJob(JobInterface job) {
        return new MarkerOptions()
                .position(new LatLng(job.getLatitude(), job.getLongitude()))
                .title(job.getJobTitle());
    }

    private void configurePanel(View rootView) {
        panel = rootView.findViewById(R.id.panel);
        panelHeader = rootView.findViewById(R.id.panel_header);
        panelHeader.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int height = bottom - top;
            panel.setTranslationY(-height);
        });
        panelHeader.setOnClickListener(v -> {
            panelInteractionHandler.toggleCollapse();
            panelInteractionHandler.animateSnap();
        });
        panel.setOnTouchListener(panelInteractionHandler);
    }

    private class PanelInteractionHandler implements View.OnTouchListener {
        private boolean isCollapsed = true;
        private Float initialY = 0f;
        private VelocityTracker tracker;
        private final float VELOCITY_CUTOFF = 200;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float headerHeight = getPanelHeaderHeight();
            float panelHeight = getPanelHeight();
            float initialPosition = isCollapsed ? headerHeight : panelHeight;
            event.offsetLocation(0, panel.getTranslationY() + initialPosition);

            float currentY = event.getY();
            if (initialY == null) initialY = currentY;

            float diff = initialY - currentY;
            float translationY = -Math.max(Math.min(initialPosition + diff, panelHeight), headerHeight);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tracker = VelocityTracker.obtain();
                    tracker.addMovement(event);
                    initialY = event.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (tracker == null) tracker = VelocityTracker.obtain();
                    tracker.addMovement(event);
                    panel.setTranslationY(translationY);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    tracker.addMovement(event);
                    tracker.computeCurrentVelocity(1000);
                    float velocity = tracker.getYVelocity();
                    tracker.recycle();
                    tracker = null;

                    if (Math.abs(velocity) >= VELOCITY_CUTOFF) {
                        isCollapsed = velocity > 0;
                    } else {
                        if (isCollapsed) {
                            isCollapsed = diff <= (panelHeight - headerHeight) / 2;
                        } else {
                            isCollapsed = -diff >= (panelHeight - headerHeight) / 2;
                        }
                    }

                    animateSnap();
                    initialY = null;
                    return true;
            }
            return false;
        }

        public void toggleCollapse() {
            this.isCollapsed = !this.isCollapsed;
        }

        public void animateSnap() {
            float headerHeight = getPanelHeaderHeight();
            float panelHeight = getPanelHeight();
            float target = isCollapsed ? -headerHeight : -panelHeight;
            ObjectAnimator animation = ObjectAnimator.ofFloat(panel, "translationY", target);
            animation.setDuration((long) (300 * Math.abs((panel.getTranslationY() - target) / (panelHeight - headerHeight))));
            animation.start();
        }

        private int getPanelHeight() {
            return panel.getMeasuredHeight();
        }

        private int getPanelHeaderHeight() {
            return panelHeader.getMeasuredHeight();
        }
    }
}