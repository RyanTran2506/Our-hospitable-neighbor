package com.example.ourhospitableneighbor.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ourhospitableneighbor.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    // Write a message to the database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;


    public LoginDataSource() {
        init();
    }

    public void init(){
        mAuth = FirebaseAuth.getInstance();

    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            AuthResult authResult = Tasks.await(mAuth.signInWithEmailAndPassword(username, password));
            FirebaseUser user = authResult.getUser();

            if (user == null) {
                return new Result.Error(new Exception("User name password are not matched"));
            } else {
                return new Result.Success<>(new LoggedInUser(user.getUid(), null));
            }



//            if(username.equals("test") && password.equals("1234")) {
//                LoggedInUser fakeUser =
//                        new LoggedInUser(
//                                java.util.UUID.randomUUID().toString(),
//                                "Jane Doe");
//                return new Result.Success<>(fakeUser);
//            }

            //return new Result.Error(new Exception("User name password are not matched"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}