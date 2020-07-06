package com.example.ourhospitableneighbor;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

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

public class PostService {
    private static PostService instance;
    private DatabaseReference collection; // firebase storage
    private Location userCurrentLocation;
    private List<Post> posts;
    private List<Post> lastPostsInAreaResult;

    public static PostService getInstance() {
        if (instance == null) instance = new PostService();
        return instance;
    }

    private PostService() {
        collection = FirebaseDatabase.getInstance().getReference("posts");
    }

    public void getAllPosts(Consumer<List<Post>> onSuccess) {
        if (posts != null) {
            onSuccess.accept(posts);
            return;
        }

        collection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    posts.add(Post.fromFirebaseSnapshot(child));
                }
                onSuccess.accept(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void getPostsInArea(LatLngBounds area, Consumer<List<Post>> onSuccess) {
        getAllPosts(allPosts -> {
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
            onSuccess.accept(postsInArea);
        });
    }

    public List<Post> getLastPostsInAreaResult() {
        return lastPostsInAreaResult;
    }

    public void setUserCurrentLocation(Location location) {
        this.userCurrentLocation = location;
    }
}
