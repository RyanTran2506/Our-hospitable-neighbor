package com.example.ourhospitableneighbor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ourhospitableneighbor.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigation();

        // Make status bar translucent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onBackPressed() {
        // Close the drawer if it's open, otherwise close the app
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void onClickCreatePost() {
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }

    private void onClickMyProfile() {
        // TODO: Navigate to a different activity
        Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
    }

    private void onClickMyPosts() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }
    private void onClickLogIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void onClickLogOut() {
        Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
    }

    private void onClickSearch() {
        Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
    }

    private void setUpNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);

        // Add more space to account for status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            toolbar.setOnApplyWindowInsetsListener((v, insets) -> {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();

                // If we used topMargin instead of leftMargin here, the topMargin will be increased
                // each time the keyboard is closed.
                layoutParams.topMargin = layoutParams.leftMargin + insets.getSystemWindowInsetTop();
                return insets;
            });
        }

        // Add the hamburger button
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        // Need to call this function to change the back icon into the hamburger icon
        toggle.syncState();

        // Add listener for pressing the search bar
        LinearLayout searchBarLayout = findViewById(R.id.search_bar_layout);
        searchBarLayout.setOnClickListener(v -> onClickSearch());

        // Add listener for pressing the log out button
        Button logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(v -> this.onClickLogOut());

        // Add listener for pressing navigation items
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_item_create_post:
                    onClickCreatePost();
                    break;
                case R.id.nav_item_my_profile:
                    onClickMyProfile();
                    break;
                case R.id.nav_item_my_posts:
                    onClickMyPosts();
                    break;
            }
            return true;
        });
    }
}