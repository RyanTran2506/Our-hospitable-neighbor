package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ourhospitableneighbor.dao.Firebase;
import com.example.ourhospitableneighbor.model.Job;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostJob extends AppCompatActivity {
    Firebase firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        firebase = new Firebase();

        EditText edtJobName = (EditText)findViewById(R.id.edtJobName);
        EditText edtJobAddress = (EditText)findViewById(R.id.edtJobAddress);
        EditText edtJobPostalCode = (EditText)findViewById(R.id.edtJobPostalCode);

        Button btnPostJob = (Button)findViewById(R.id.btnPostJob);
        btnPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Job newJob = new Job();
                newJob.setJobTitle(edtJobName.getText().toString());
                newJob.setAddress(edtJobAddress.getText().toString());

                firebase.pushJob(newJob);
                Toast.makeText(PostJob.this, "posted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}