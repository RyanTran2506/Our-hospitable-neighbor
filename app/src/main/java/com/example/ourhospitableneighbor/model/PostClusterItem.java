package com.example.ourhospitableneighbor.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PostClusterItem implements ClusterItem {
    private Post post;

    public PostClusterItem(Post post) {
        this.post = post;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        //return new LatLng(post.getLatitude(), post.getLongitude());
        return new LatLng(post.getCoords().getLat(), post.getCoords().getLng());
    }

    @Nullable
    @Override
    public String getTitle() {
        return post.getPostTitle();
    }

    @Nullable
    @Override
    public String getSnippet() {
        return post.getAddress();
    }

    public String getThumbnail() {
        return post.getThumbnail();
    }

    public Post getPost() {
        return post;
    }
}
