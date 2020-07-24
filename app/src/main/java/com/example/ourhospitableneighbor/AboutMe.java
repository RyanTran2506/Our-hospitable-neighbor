package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ourhospitableneighbor.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AboutMe extends AppCompatActivity {
    TextView txtName, txtDOB, txtEmail, txtPhoneNumber;
    User user;

     TextView fstTxt = null;
     DatabaseReference databaseReference;
     TextView profileNameTextView;
     TextView profilePhonenoTextView;
     TextView profileDOBTextView;
     TextView profileEmailTextView;
     FirebaseAuth firebaseAuth;
     FirebaseDatabase firebaseDatabase;
     ImageView profilePicImageView;
     FirebaseStorage firebaseStorage;

    private EditText editTextName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);


        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        profileNameTextView = findViewById(R.id.profileName);
        profileEmailTextView = findViewById(R.id.profileEmail);
        profilePhonenoTextView = findViewById(R.id.profilePhone);
        profileDOBTextView=findViewById(R.id.profileBirth);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        getUser();
    }

    private void getUser(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = User.fromFirebaseSnapshot(snapshot);

                profileNameTextView.setText(user.getName());
                profileDOBTextView.setText(user.getDob());
                profileEmailTextView.setText(firebaseAuth.getCurrentUser().getEmail());
                profilePhonenoTextView.setText(user.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void navigateLogOut(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}