package com.example.ourhospitableneighbor.ui.login;

import android.os.AsyncTask;

import androidx.lifecycle.ViewModelProviders;

public class LoginAsyncTask extends AsyncTask<String, Integer, Void> {

    @Override
    protected Void doInBackground(String... strings) {

//        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
//                .get(LoginViewModel.class);

        System.out.println(strings[0]);
        System.out.println(strings[1]);
        String userName = strings[0];
        String password = strings[1];
//        loginViewModel.login(userName, password)
        return null;
    }
}
