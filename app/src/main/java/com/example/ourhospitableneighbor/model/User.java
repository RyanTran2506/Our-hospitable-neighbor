package com.example.ourhospitableneighbor.model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String avatarLink;
    private String dob;
    private String phoneNumber;

    public User(){
        this.name = "";
        this.avatarLink = "";
        this.dob  = "";
        this.phoneNumber = "";
    }

    public User(String name, String avatarLink, String dob, String phoneNumber){
        this.name = name;
        this.avatarLink = avatarLink;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
    }

    public static User fromFirebaseSnapshot(DataSnapshot doc) {
        // TODO: handle the remaining fields
        User user = new User();
        user.setName(doc.child("name").getValue(String.class));
        user.setAvatarLink(doc.child("avatarLink").getValue(String.class));
        user.setDob(doc.child("dob").getValue(String.class));
        user.setPhoneNumber(doc.child("phoneNumber").getValue(String.class));

        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
