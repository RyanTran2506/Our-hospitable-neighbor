package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.ourhospitableneighbor.dao.Firebase;

public class CreatePostActivity extends AppCompatActivity {
    Firebase firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

    }
}