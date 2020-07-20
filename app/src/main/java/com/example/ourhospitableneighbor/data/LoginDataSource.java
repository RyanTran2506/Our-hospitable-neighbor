package com.example.ourhospitableneighbor.data;

import com.example.ourhospitableneighbor.data.model.LoggedInUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    // Write a message to the database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public void init(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        LoggedInUser user = new LoggedInUser("test2","emmatrrr");
        myRef.push().setValue(user);
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            if(username.equals("test") && password.equals("1234")) {
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                "Jane Doe");
                return new Result.Success<>(fakeUser);
            }

            return new Result.Error(new Exception("User name password are not matched"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}