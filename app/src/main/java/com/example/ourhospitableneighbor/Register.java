package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourhospitableneighbor.model.User;
import com.example.ourhospitableneighbor.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Register extends AppCompatActivity {

    EditText rEmail;
    EditText rPwd;
    EditText rName;
    EditText rDOB;
    EditText rPhone;
    EditText rConfirmEmail;
    Button btnrSignUp;
    FirebaseAuth mAuth;
    TextView linkrLogin;
    ProgressBar loadingProgressBar;
    final static String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rEmail = findViewById(R.id.txtEmail);
        rName = findViewById(R.id.txtName);
        rDOB = findViewById(R.id.txtDOB);
        rPhone = findViewById(R.id.txtPhoneReg);
        rPwd = findViewById(R.id.txtPhoneNumber);
        rConfirmEmail = findViewById(R.id.txtConfirmPwd);
        btnrSignUp = findViewById(R.id.btnSignup);
        linkrLogin = findViewById(R.id.linkLogin);
        loadingProgressBar = findViewById(R.id.loading);

        linkrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }


    public void goToLogin() {
        Intent intent = new Intent(Register.this, LoginActivity.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.i(TAG, user.getDisplayName());
            goToLogin();
        } else {
            Log.i(TAG, "User null");
        }
    }


    public void onSignUpClick(View v) {

        String fullName = rName.getText().toString();
        String email = rEmail.getText().toString();
        String password = rPwd.getText().toString();
        String dob = rDOB.getText().toString();
        String phone = rPhone.getText().toString();

        if (TextUtils.isEmpty(email)) {
            rEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            rPwd.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            rName.setError("Full name is required");
            return;
        }
        if (password.length() < 6) {
            rPwd.setError("Password mus be more than 6 characters");
        }
        if (TextUtils.isEmpty(phone)) {
            rPhone.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(dob)) {
            rDOB.setError("Email is required");
            return;
        }
        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users").child(user.getUid());
                            User newUser = new User(fullName, "avatars/default.jpg", dob, phone);
                            myRef.setValue(newUser);
                            //myRef.child("name").setValue(rName.getText().toString());

                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            //goToLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void getAvatarImages() {
        StorageReference ref = FirebaseStorage.getInstance().getReference("avatars");
        ref.listAll().addOnSuccessListener(result -> {
            for (StorageReference fileRef : result.getItems()) {

            }
        });
    }
}