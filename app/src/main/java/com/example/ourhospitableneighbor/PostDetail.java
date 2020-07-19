package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourhospitableneighbor.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostDetail extends AppCompatActivity {
    String postID;
    Post post;

    TextView txtPostTitle, txtPostAddress, txtPostWage,txtPostContact, txtPostDesc;
    GridView grdImages;
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
        grdImages = findViewById(R.id.grdPostDetail_Photos);

        setImgs();

        //Change it back later - Ryan
        String userID = "Ryan";

        btnBack = findViewById(R.id.btnPostDetail_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnTakeJob = findViewById(R.id.btnPostDetail_Accept);
        if(post.getOwnerID().equals(userID)){
            btnTakeJob.setClickable(false);
            btnTakeJob.setBackgroundResource(R.drawable.button_disable);
        }
        else
        {
            btnTakeJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post.setWorkerID(userID);
                    PostService.getInstance().updatePost(post);
                    Intent intent = new Intent(PostDetail.this, History.class);
                    startActivity(intent);
                }
            });
        }

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
                File localFile = File.createTempFile("image"+i, "jpg");
                imgRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        imgPaths.add(Uri.fromFile(localFile));
                        ImageAdapter imgAdapter = new ImageAdapter(PostDetail.this, imgPaths);
                        grdImages.setAdapter(imgAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(PostDetail.this, "Something wrong on downloading images",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}