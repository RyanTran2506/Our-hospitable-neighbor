package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AboutMe extends AppCompatActivity {

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
        fstTxt = findViewById(R.id.fstTxt);
        modifyTextView("alo");


        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileNameTextView = findViewById(R.id.profileName);
        profileEmailTextView = findViewById(R.id.profileEmail);
        profilePhonenoTextView = findViewById(R.id.profilePhone);
        profileDOBTextView=findViewById(R.id.profileBirth);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference storageReference = firebaseStorage.getReference();
    }

    private void modifyTextView(String text){
        fstTxt.setText(text);
    }

    public void navigateLogOut(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}