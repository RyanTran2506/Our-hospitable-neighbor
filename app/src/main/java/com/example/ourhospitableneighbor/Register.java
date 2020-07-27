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

import com.example.ourhospitableneighbor.model.Post;
import com.example.ourhospitableneighbor.model.User;
import com.example.ourhospitableneighbor.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Register<mDatabase, postListener> extends AppCompatActivity {

    EditText rEmail;
    EditText rPwd;
    EditText rName;
    EditText rDOB;
    EditText rPhone;
    EditText rConfirmPass;
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
        rConfirmPass = findViewById(R.id.txtConfirmPwd);
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
        Intent intent = new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Log.i(TAG, user.getDisplayName());
            goToLogin();
        } else {
            Log.i(TAG, "User null");
        }
    }


    private boolean isValidatedUser(User newUser) {

        String password = rPwd.getText().toString();
        String confirmPassword = rConfirmPass.getText().toString();

        if (TextUtils.isEmpty(newUser.getEmail())) {
            rEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            rPwd.setError("Password is required");
            return false;
        }

        if (TextUtils.isEmpty(newUser.getName())) {
            rName.setError("Full name is required");
            return false;
        }
        if (password.length() < 5) {
            rPwd.setError("Password mus be more than 5 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            rPwd.setError("Confirm password must match");
            return false;
        }
        if (TextUtils.isEmpty(newUser.getPhoneNumber())) {
            rPhone.setError("Phone is required");
            return false;
        }
        if (TextUtils.isEmpty(newUser.getDob())) {
            rDOB.setError("Date is required");
            return false;
        }

        return true;
    }

    public void onSignUpClick(View v) {

        String fullName = rName.getText().toString();
        String email = rEmail.getText().toString();
        String dob = rDOB.getText().toString();
        String phone = rPhone.getText().toString();
        String password = rPwd.getText().toString();

        User newUser = new User(fullName, email, dob, phone);

        if (!isValidatedUser(newUser)) {
            return;
        }

        loadingProgressBar.setVisibility(View.GONE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //save to firebase
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users").child(user.getUid());
                            User newUser = new User(fullName, email, dob, phone);
                            myRef.setValue(newUser);
                            //myRef.child("name").setValue(rName.getText().toString());
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                            goToLogin();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private DatabaseReference mDatabase;
// ...
//    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance().getReference()

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Post post = dataSnapshot.getValue(Post.class);
            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };
//   mPostReference.addValueEventListener(postListener);




}