package com.example.ourhospitableneighbor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.ourhospitableneighbor.helper.Debouncer;
import com.example.ourhospitableneighbor.helper.SizeConverter;
import com.example.ourhospitableneighbor.model.Post;
import com.example.ourhospitableneighbor.model.PostClusterItem;
import com.example.ourhospitableneighbor.view.PanelView;
import com.example.ourhospitableneighbor.view.PostInfoWindowView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.Disposable;

public class PostsMapFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private PanelView panel;

    private Debouncer showPostsDebouncer = new Debouncer();
    private FusedLocationProviderClient locationClient;
    private PostService postService = PostService.getInstance();
    private ClusterManager<PostClusterItem> clusterManager;

    private Disposable allJobsDisposable;
    private Disposable jobsInAreaDisposable;

    private boolean loadingPostsFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_map, container, false);
        panel = rootView.findViewById(R.id.panel);
        locationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        addMapFragment();

        jobsInAreaDisposable = postService.getPostInAreaObservable().subscribe(posts -> {
            panel.setPosts(posts);
            if (loadingPostsFirstTime) {
                panel.setCollapse(false, true);
                loadingPostsFirstTime = false;
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (allJobsDisposable != null) allJobsDisposable.dispose();
        if (jobsInAreaDisposable != null) jobsInAreaDisposable.dispose();
        super.onDestroy();
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

        Context ctx = getContext();
        assert ctx != null;

        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation().addOnSuccessListener(postService::setUserCurrentLocation);
            setUpMyLocationButton();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        // Configure cluster manager
        clusterManager = new ClusterManager<>(ctx, map);
        clusterManager.setRenderer(new DefaultClusterRenderer<PostClusterItem>(ctx, map, clusterManager) {
            @Override
            protected void onClusterItemRendered(@NonNull PostClusterItem clusterItem, @NonNull Marker marker) {
                super.onClusterItemRendered(clusterItem, marker);
                marker.setTag(clusterItem.getPost());
            }
        });
        clusterManager.getMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                PostInfoWindowView v = new PostInfoWindowView(ctx);
                Post p = (Post) marker.getTag();
                if (p == null) return null;
                v.setPost(p);
                v.setMarker(marker);
                return v;
            }
        });

        map.setOnCameraIdleListener(() -> {
            this.updateAreaDebounced();
            clusterManager.onCameraIdle();
        });
        map.setOnMarkerClickListener(clusterManager);

        if (allJobsDisposable != null) allJobsDisposable.dispose();
        allJobsDisposable = postService.getAllPostsObservable().subscribe(posts -> {
            clusterManager.clearItems();
            for (Post post : posts) {
                PostClusterItem options = createMarkerFromPost(post);
                if (options != null) clusterManager.addItem(options);
                clusterManager.cluster();
            }
        });
        // End configure cluster manager

        map.getUiSettings().setMapToolbarEnabled(false);

        setUpCompassButton();
        setInitialViewPoint();
        fetchAllPosts();
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

        View toolbar = getToolbar();
        if (toolbar == null) return;

        int padding = SizeConverter.fromDpToPx(getResources(), 10);

        // Adjust the button's margin so that it's right below the toolbar
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.setMargins(0, toolbar.getMeasuredHeight() + ((ViewGroup.MarginLayoutParams) toolbar.getLayoutParams()).topMargin + padding, 0, 0);
        locationButton.setLayoutParams(rlp);

        // Adjust the button's margin again every time the toolbar's layout change
        toolbar.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    RelativeLayout.LayoutParams _rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    _rlp.setMargins(0, bottom + padding, 0, 0);
                    locationButton.setLayoutParams(_rlp);
                }
        );
    }

    private void setUpCompassButton() {
        View mapView = mapFragment.getView();
        if (mapView == null) return;

        View compassButton = mapView.findViewWithTag("GoogleMapCompass");
        if (compassButton == null) return;

        View toolbar = getToolbar();
        if (toolbar == null) return;

        int padding = SizeConverter.fromDpToPx(getResources(), 10);

        // Adjust the button's margin so that it's right below the toolbar
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
        rlp.setMargins(0, toolbar.getMeasuredHeight() + ((ViewGroup.MarginLayoutParams) toolbar.getLayoutParams()).topMargin + padding, 0, 0);
        compassButton.setLayoutParams(rlp);

        // Adjust the button's margin again every time the toolbar's layout change
        toolbar.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    RelativeLayout.LayoutParams _rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
                    _rlp.setMargins(0, bottom + padding, 0, 0);
                    compassButton.setLayoutParams(_rlp);
                }
        );
    }

    private void updateAreaDebounced() {
        showPostsDebouncer.debounce(this::updateArea, 200, TimeUnit.MILLISECONDS);
    }

    @SuppressLint("MissingPermission")
    private void updateArea() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
            postService.setArea(bounds);
        });
    }

    private void fetchAllPosts() {
        postService.getAllPosts();
    }

    private PostClusterItem createMarkerFromPost(Post post) {
        Double lat = post.getCoords().getLat();
        Double lng = post.getCoords().getLng();
        if (lat == null || lng == null) return null;

        return new PostClusterItem(post);
    }

    private View getToolbar() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return activity.findViewById(R.id.toolbar_main);
    }
}