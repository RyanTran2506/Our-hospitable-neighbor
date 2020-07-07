package com.example.ourhospitableneighbor.dao;

import android.util.Log;
import com.example.ourhospitableneighbor.model.Job;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {
    private DatabaseReference ref;
    public Firebase(){
        ref = FirebaseDatabase.getInstance().getReference();
    }
    public void pushJob(Job job){
        ref.child("Jobs").child("job").setValue(job);

    }
}
