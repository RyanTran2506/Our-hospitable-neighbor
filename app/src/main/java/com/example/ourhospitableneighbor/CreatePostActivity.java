package com.example.ourhospitableneighbor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.ourhospitableneighbor.model.Coords;
import com.example.ourhospitableneighbor.model.Post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 25;

    private ArrayList<Uri> imgPaths;

    EditText edtPostTitle, edtPostAddress, edtPostWage, edtPostDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        edtPostTitle = findViewById(R.id.edtPostTitle);
        edtPostAddress = findViewById(R.id.edtPostAddress);
        edtPostWage = findViewById(R.id.edtPostWage);
        edtPostDescription = findViewById(R.id.edtPostDescription);
        Button btnPost = findViewById(R.id.btnPostPost);
        Button btnUploadPhoto = findViewById(R.id.btnUploadPhoto);

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotos();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }
    private void showPhotos(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                // Get the Image from data
                imgPaths = new ArrayList<Uri>();
                if(data.getClipData()!=null){
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        imgPaths.add(uri);
                    }
                }
                if(data.getData()!= null){
                    Uri uri = data.getData();
                    imgPaths.add(uri);
                }

                GridView imgGridView = findViewById(R.id.grdPhotos);
                ImageAdapter imgAdapter = new ImageAdapter(CreatePostActivity.this, imgPaths);
                imgGridView.setAdapter(imgAdapter);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void post(){
        String postTitle = edtPostTitle.getText().toString();
        String postAddress = edtPostAddress.getText().toString();
        int postWage = Integer.parseInt(edtPostWage.getText().toString());
        String postDes = edtPostDescription.getText().toString();
        //int id = PostService.getInstance().getMaxID();
        String id = UUID.randomUUID().toString();

        Post post = new Post();
        post.setPostID(id);
        post.setPostTitle(postTitle);
        post.setAddress(postAddress);
        post.setWage(postWage);
        post.setDescription(postDes);
        post.setCoords(new Coords(getLatitudeFromAddress(postAddress), getLongitudeFromAddress(postAddress)));
        post.setImageIDs(convertLocalImgToFirebase(imgPaths, post));
        //change it back later
        post.setOwnerID("temp");

        PostService.getInstance().add(post);

        Toast.makeText(CreatePostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();

        Intent homeIntent = new Intent(CreatePostActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
    private List<String> convertLocalImgToFirebase(ArrayList<Uri> imgPaths, Post post) {
        List<String> result = new ArrayList<String>();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images");

        String folder = String.valueOf(post.getPostID());
        if(imgPaths != null){
            for (int i = 1; i <= imgPaths.size(); i++){
                StorageReference fileRef = ref.child(folder).child(i +".jpg");
                fileRef.putFile(imgPaths.get(i-1));
                result.add(folder+"/"+i+".jpg");
            }
        }
        else{
            result.add("location.jpg");
        }
        return result;
    }

    public double getLatitudeFromAddress(String strAddress){
        List<Address> addresses;
        double latitude;
        try{
            Geocoder coder = new Geocoder(this);
            addresses = coder.getFromLocationName(strAddress,5);
            Address location=addresses.get(0);
            latitude = location.getLatitude();
        }
        catch (Exception e){
            latitude = 0;
            e.getMessage();
        }
        return latitude;
    }

    public double getLongitudeFromAddress(String strAddress){

        List<Address> addresses;
        double longitude;
        try{
            Geocoder coder = new Geocoder(this);
            addresses = coder.getFromLocationName(strAddress,5);
            Address location=addresses.get(0);
            longitude = location.getLongitude();
        }
        catch (Exception e){
            longitude = 0;
            e.getMessage();
        }
        return longitude;
    }
}