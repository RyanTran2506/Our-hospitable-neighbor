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

    EditText edtPostTitle, edtPostAddress, edtPostWage, edtPostDescription, edtContactPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        edtPostTitle = findViewById(R.id.edtPostTitle);
        edtPostAddress = findViewById(R.id.edtPostAddress);
        edtPostWage = findViewById(R.id.edtPostWage);
        edtPostDescription = findViewById(R.id.edtPostDescription);
        edtContactPhoneNumber = findViewById(R.id.edtContactPhoneNumber);
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
        String id = UUID.randomUUID().toString();
        String postTitle = edtPostTitle.getText().toString();
        String postAddress = edtPostAddress.getText().toString();
        String postWage = edtPostWage.getText().toString();
        String postDes = edtPostDescription.getText().toString();
        String contactPhoneNumber = edtContactPhoneNumber.getText().toString();

        //Temp - change back later - Ryan
        String ownerID = "Ryan";
        String workerID = "";


        if(validateInput(postTitle, postAddress, contactPhoneNumber, postWage)==true){
            Post post = new Post();
            post.setPostID(id);
            post.setPostTitle(postTitle);
            post.setAddress(postAddress);
            post.setWage(Integer.parseInt(postWage));
            post.setDescription(postDes);
            post.setCoords(getCoordsFromAddress(postAddress));
            post.setContactPhoneNumber(contactPhoneNumber);
            post.setImageIDs(convertLocalImgToFirebase(imgPaths, post));
            post.setOwnerID(ownerID);
            post.setWorkerID(workerID);

            PostService.getInstance().add(post);

            Toast.makeText(CreatePostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();

            Intent homeIntent = new Intent(CreatePostActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
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

    public Coords getCoordsFromAddress(String strAddress){
        List<Address> addresses;
        Coords coords = null;
        try{
            Geocoder coder = new Geocoder(this);
            addresses = coder.getFromLocationName(strAddress,5);
            Address location=addresses.get(0);
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            coords = new Coords(latitude,longitude);
        }
        catch (Exception e){
            e.getMessage();
        }
        return coords;
    }

    private boolean validateInput(String postTitle, String postAddress, String postWage, String contactPhoneNumber){
        if(postTitle.isEmpty()){
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(postAddress.isEmpty()){
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(contactPhoneNumber.isEmpty()){
            Toast.makeText(this, "Contact Phone Number cannot be empty", Toast.LENGTH_SHORT);
            return false;
        }
        if(postWage.isEmpty()){
            Toast.makeText(this, "Wage cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}