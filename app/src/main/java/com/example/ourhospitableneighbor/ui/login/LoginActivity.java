package com.example.ourhospitableneighbor.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourhospitableneighbor.MainActivity;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.Register;
import com.example.ourhospitableneighbor.dao.Firebase;
import com.example.ourhospitableneighbor.data.LoginDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    TextView login_register;
    final EditText usernameEditText = findViewById(R.id.login_txtEmail);
    final EditText passwordEditText = findViewById(R.id.login_txtPwd);
    final Button loginButton = findViewById(R.id.login_btnLogin);
    final ProgressBar loadingProgressBar = findViewById(R.id.loading);
    FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        mAuth= FirebaseAuth.getInstance();



        login_register = findViewById(R.id.linkLogin);
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDataSource loginDataSource = new LoginDataSource();
                loginDataSource.init();
                Intent intent = new Intent(LoginActivity.this, Register.class);
                startActivity(intent);
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
EditText lEmail= findViewById(R.id.login_txtEmail);
EditText lPass=findViewById(R.id.login_txtPwd);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
String email=lEmail.getText().toString().trim();
String pass= lPass.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    lEmail.setError("Email is required");
                    return ;
                }

                if(TextUtils.isEmpty(pass)){
                    lPass.setError("Password is required");
                    return ;
                }


                if (pass.length()<6){
                    lPass.setError("Password mus be more than 6 characters");
                }
                loadingProgressBar.setVisibility(View.VISIBLE);
                //how to put afterTextChanged


                mAuth.signInWithEmailAndPassword( email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(LoginActivity.this,"Log in succesfullly",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                            }
                        }

//                Executors.newSingleThreadExecutor().submit(()
//                        -> loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString()));
//
//                System.out.println("--------------"+loginViewModel.getLoginResult());

        });
    }

////    private void updateUiWithUser(LoggedInUserView model) {
////        String welcome = getString(R.string.welcome) + model.getDisplayName();
////        // TODO : initiate successful logged in experience
////        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
////        Intent intent = new Intent(this, MainActivity.class);
////        startActivity(intent);
////    }
//
////    private void showLoginFailed(@StringRes Integer errorString) {
////        System.out.println("sssss");
//       // Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
////        Intent intent = new Intent(this, MainActivity.class);
////        startActivity(intent);
////        super.onResume();
//
////    }
////
////    @Override
////    protected void onPause() {
////        super.onPause();
////        super.onResume();
////        System.out.println("ddd");
////        Toast.makeText(getApplicationContext(),"Wrong Email/ Password",Toast.LENGTH_SHORT).show();
////        //TODO: Mai sua

    }
}