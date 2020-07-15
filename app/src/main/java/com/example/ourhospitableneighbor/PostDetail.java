package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourhospitableneighbor.model.Post;

public class PostDetail extends AppCompatActivity {
    String postID;
    Post post;

    TextView txtPostTitle, txtPostAddress, txtPostWage, txtPostDesc;
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
        txtPostDesc = findViewById(R.id.txtPostDetail_Desc);
        imgs = findViewById(R.id.grdPostDetail_Photos);

        btnBack = findViewById(R.id.btnPostDetail_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(post != null){
            txtPostTitle.setText(post.getPostTitle());
            txtPostAddress.setText(post.getAddress());
            //txtPostWage.setText(post.getWage());
            txtPostDesc.setText(post.getDescription());


        }
    }
}