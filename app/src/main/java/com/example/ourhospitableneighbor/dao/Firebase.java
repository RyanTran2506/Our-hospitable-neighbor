package com.example.ourhospitableneighbor.dao;

import com.example.ourhospitableneighbor.model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {
    private DatabaseReference ref;
    public Firebase(){
        ref = FirebaseDatabase.getInstance().getReference();
    }
    public void pushPost(Post post){
        ref.child("Posts").child("post").setValue(post);

    }
}
