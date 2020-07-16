package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourhospitableneighbor.model.Post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PostDetail extends AppCompatActivity {
    String postID;
    Post post;

    TextView txtPostTitle, txtPostAddress, txtPostWage,txtPostContact, txtPostDesc;
    GridView imgs;
    Button btnTakeJob, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postID = getIntent().getExtras().getString("postID");
        post = PostService.getInstance().getPost(postID);

        txtPostTitle = findViewById(R.id.txtPostDetail_Title);
        txtPostAddress = findViewById(R.id.txtPostDetail_Address);
        txtPostWage = findViewById(R.id.txtPostDetail_Wage);
        txtPostContact = findViewById(R.id.txtPostDetail_Contact);
        txtPostDesc = findViewById(R.id.txtPostDetail_Desc);
        imgs = findViewById(R.id.grdPostDetail_Photos);

        setImgs();

        btnBack = findViewById(R.id.btnPostDetail_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnTakeJob = findViewById(R.id.btnPostDetail_Accept);
        btnTakeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if(post != null){
            txtPostTitle.setText(post.getPostTitle());
            txtPostAddress.setText(post.getAddress());
            txtPostWage.setText(String.valueOf(post.getWage()));
            txtPostContact.setText(post.getContactPhoneNumber());
            txtPostDesc.setText(post.getDescription());
        }
    }
    private void setImgs(){
        try {
            ArrayList<Uri> imgPaths = new ArrayList<Uri>();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(post.getPostID());
            for (int i = 1; i <= post.getImageIDs().size(); i++){
                StorageReference imgRef = ref.child(i+".jpg");
                File localFile = File.createTempFile(post.getPostID()+"/"+i, "jpg");
                imgRef.getFile(localFile);
                imgPaths.add(Uri.fromFile(localFile));
            }
            GridView imgGridView = findViewById(R.id.grdPostDetail_Photos);
            ImageAdapter imgAdapter = new ImageAdapter(PostDetail.this, imgPaths);
            imgGridView.setAdapter(imgAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}