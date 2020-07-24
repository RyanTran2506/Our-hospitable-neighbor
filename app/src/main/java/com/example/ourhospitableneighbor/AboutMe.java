package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourhospitableneighbor.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutMe extends AppCompatActivity {
    TextView txtName, txtDOB, txtEmail, txtPhoneNumber;
    User user;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        txtName = findViewById(R.id.txtName);
        txtDOB = findViewById(R.id.txtDOB);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);

        getUser();
    }

    private void getUser(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = User.fromFirebaseSnapshot(snapshot);

                txtName.setText(user.getName());
                txtDOB.setText(user.getDob());
                txtEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                txtPhoneNumber.setText(user.getPhoneNumber());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}