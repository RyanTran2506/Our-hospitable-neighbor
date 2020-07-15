package com.example.ourhospitableneighbor.model;

import android.location.Location;

import com.google.firebase.database.DataSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Post {
    private String postID;
    private String postTitle;
    private PostStatus status;
    private String address;
    //private Double latitude;
    //private Double longitude;
    private Coords coords;
    private String ownerID;
    private LocalDate date;
    private List<String> imageIDs;
    private int wage;
    private Float distanceFromUserLocation;

    private String description;

    public static Post fromFirebaseSnapshot(DataSnapshot doc) {
        // TODO: handle the remaining fields
        Post post = new Post();
        post.setPostID(doc.getKey());
        post.setPostTitle(doc.child("postTitle").getValue(String.class));
        post.setAddress(doc.child("address").getValue(String.class));
        post.setOwnerID(doc.child("ownerID").getValue(String.class));

        List<String> imageIDs = new ArrayList<>();
        for (DataSnapshot c : doc.child("imageIDs").getChildren()) {
            imageIDs.add(c.getValue(String.class));
        }
        post.setImageIDs(imageIDs);

//        post.setLatitude(doc.child("coords/lat").getValue(Double.class));
//        post.setLongitude(doc.child("coords/lng").getValue(Double.class));

        post.setCoords(new Coords(doc.child("coords/lat").getValue(Double.class),doc.child("coords/lng").getValue(Double.class)));
//        post.coords.setLat(doc.child("coords/lat").getValue(Double.class));
//        post.coords.setLng(doc.child("coords/lng").getValue(Double.class));

        return post;
    }
    public void updateWithPost(Post p) {
        setPostID(p.getPostID());
        setPostTitle(p.getPostTitle());
        setStatus(p.getStatus());
        setAddress(p.getAddress());
//        setLatitude(p.getLatitude());
//        setLongitude(p.getLongitude());
        setOwnerID(p.getOwnerID());
        setDate(p.getDate());
        setImageIDs(p.getImageIDs());
        setCoords(p.getCoords());
//        setPaymentType(p.getPaymentType());
//        setExpectedHrs(p.getExpectedHrs());
//        setRate(p.getRate());
//        setTotalPay(p.getTotalPay());
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


//    public Double getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(Double latitude) {
//        this.latitude = latitude;
//    }
//
//    public Double getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(Double longitude) {
//        this.longitude = longitude;
//    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getImageIDs() {
        return imageIDs;
    }

    public void setImageIDs(List<String> imageIDs) {
        this.imageIDs = imageIDs;
    }

    public int getWage() {
        return wage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setWage(int wage) {
        this.wage = wage;
    }

    public String getThumbnail() {
        List<String> imageIDs = getImageIDs();
        if (imageIDs == null || imageIDs.isEmpty()) return null;
        return imageIDs.get(0);
    }

    public void setUserCurrentLocation(Location location) {
        if (location == null) return;
        distanceFromUserLocation = getDistanceFromLocation(location);
    }

    public Float getDistanceFromUserLocation() {
        return this.distanceFromUserLocation;
    }

    private float getDistanceFromLocation(Location location) {
        float[] result = new float[1];
        //Location.distanceBetween(location.getLatitude(), location.getLongitude(), this.getLatitude(), this.getLongitude(), result);
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), this.getCoords().getLat(), this.getCoords().getLng(), result);
        return result[0];
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }
}
