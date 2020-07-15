package com.example.ourhospitableneighbor;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ourhospitableneighbor.model.Post;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import java9.util.Optional;
import java9.util.concurrent.CompletableFuture;

public class PostService {
    private static PostService instance;
    private List<Post> posts = new ArrayList<>();
    private Map<String, Post> postsByID = new HashMap<>();

    private DatabaseReference collection; // firebase storage

    // Rx variables
    private final BehaviorSubject<List<Post>> allPostsSubject = BehaviorSubject.create();
    private final BehaviorSubject<List<Post>> postsInAreaSubject = BehaviorSubject.create();
    private final BehaviorSubject<LatLngBounds> areaSubject = BehaviorSubject.create();
    // Location might be null, but RxJava doesn't like null so we must wrap it with Optional
    private final BehaviorSubject<Optional<Location>> currentLocationSubject = BehaviorSubject.createDefault(Optional.empty());

    private CompletableFuture<List<Post>> getAllPostsFuture;

    public static PostService getInstance() {
        if (instance == null) instance = new PostService();
        return instance;
    }

    private PostService() {
        collection = FirebaseDatabase.getInstance().getReference("posts");

        Observable.combineLatest(allPostsSubject, areaSubject, currentLocationSubject, (posts, area, location) -> {
            List<Post> postsInArea = new ArrayList<>();
            for (Post post : posts) {
//                Double lat = post.getLatitude();
//                Double lng = post.getLongitude();
                Double lat = post.getCoords().getLat();
                Double lng = post.getCoords().getLng();

                if (lat == null || lng == null) continue;
                
                post.setUserCurrentLocation(location.orElse(null));
                if (lat >= area.southwest.latitude && lat <= area.northeast.latitude && lng >= area.southwest.longitude && lng <= area.northeast.longitude) {
                    postsInArea.add(post);
                }
            }

            // Have to do this check else the sort function will never complete
            if (location.isPresent()) {
                Collections.sort(postsInArea, (o1, o2) -> Float.compare(o1.getDistanceFromUserLocation(), o2.getDistanceFromUserLocation()));
            }

            return postsInArea;
        }).subscribe(postsInAreaSubject);
    }

    public CompletableFuture<List<Post>> getAllPosts() {
        if (getAllPostsFuture != null) {
            return getAllPostsFuture;
        }

        getAllPostsFuture = new CompletableFuture<>();
        collection.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                postsByID.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (postsByID.containsKey(snapshot.getKey())) continue;
                    Post p = Post.fromFirebaseSnapshot(child);
                    posts.add(p);
                    postsByID.put(p.getPostID(), p);
                }
                getAllPostsFuture.complete(posts);

                collection.removeEventListener(firebaseChildEventListener);
                allPostsSubject.onNext(posts);
                collection.addChildEventListener(firebaseChildEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return getAllPostsFuture;
    }

    /**
     * This observable emits a new event asynchronously after the method {@link #getAllPosts} is called
     */
    public Observable<List<Post>> getAllPostsObservable() {
        return allPostsSubject;
    }

    /**
     * This observable emits a new event whenever the camera's area or the user location change.
     * To change the area, call the {@link #setArea} method.
     * To change the user's current location, call the {@link #setUserCurrentLocation} method
     */
    public Observable<List<Post>> getPostInAreaObservable() {
        return postsInAreaSubject;
    }

    public List<Post> getLastPostsInAreaResult() {
        return postsInAreaSubject.getValue();
    }

    public void setArea(LatLngBounds area) {
        areaSubject.onNext(area);
    }

    public void setUserCurrentLocation(Location location) {
        currentLocationSubject.onNext(Optional.of(location));
    }

    public void add(Post post){
        //collection.push().setValue(post);
        collection.child(post.getPostID()).setValue(post);
    }

    private final ChildEventListener firebaseChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (postsByID.containsKey(snapshot.getKey())) return;
            Post p = Post.fromFirebaseSnapshot(snapshot);
            posts.add(p);
            postsByID.put(p.getPostID(), p);
            allPostsSubject.onNext(posts);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Post p = Post.fromFirebaseSnapshot(snapshot);
            String id = p.getPostID();
            if (postsByID.containsKey(id)) {
                Objects.requireNonNull(postsByID.get(id)).updateWithPost(p);
                allPostsSubject.onNext(posts);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            String id = snapshot.getKey();
            if (postsByID.containsKey(id)) {
                postsByID.remove(id);

                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).getPostID().equals(id)) {
                        posts.remove(i);
                        allPostsSubject.onNext(posts);
                        return;
                    }
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };
}
