package com.example.ourhospitableneighbor;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.ourhospitableneighbor.model.Post;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java9.util.concurrent.CompletableFuture;

public class PostService {
    private static PostService instance;
    private DatabaseReference collection; // firebase storage
    private Location userCurrentLocation;
    private List<Post> posts;
    private List<Post> lastPostsInAreaResult;

    private CompletableFuture<List<Post>> getAllPostsFuture;

    public static PostService getInstance() {
        if (instance == null) instance = new PostService();
        return instance;
    }

    private PostService() {
        collection = FirebaseDatabase.getInstance().getReference("posts");
    }

    public CompletableFuture<List<Post>> getAllPosts() {
        if (getAllPostsFuture != null) {
            return getAllPostsFuture;
        }

        getAllPostsFuture = new CompletableFuture();
        collection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    posts.add(Post.fromFirebaseSnapshot(child));
                }
                getAllPostsFuture.complete(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return getAllPostsFuture;
    }


    public CompletableFuture<List<Post>> getPostsInArea(LatLngBounds area) {
        return getAllPosts().thenApplyAsync(allPosts -> {
            List<Post> postsInArea = new ArrayList<>();

            for (Post post : allPosts) {
                double lat = post.getLatitude();
                double lng = post.getLongitude();
                if (lat >= area.southwest.latitude && lat <= area.northeast.latitude && lng >= area.southwest.longitude && lng <= area.northeast.longitude) {
                    postsInArea.add(post);
                    post.setUserCurrentLocation(userCurrentLocation);
                }
            }

            // Have to do this check else the sort function will never complete
            if (userCurrentLocation != null) {
                Collections.sort(postsInArea, (o1, o2) -> Float.compare(o1.getDistanceFromUserLocation(), o2.getDistanceFromUserLocation()));
            }

            lastPostsInAreaResult = postsInArea;
            return postsInArea;
        });
    }

    public List<Post> getLastPostsInAreaResult() {
        return lastPostsInAreaResult;
    }

    public void setUserCurrentLocation(Location location) {
        this.userCurrentLocation = location;
    }
}
