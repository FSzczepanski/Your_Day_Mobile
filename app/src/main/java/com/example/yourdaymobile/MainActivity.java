package com.example.yourdaymobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private static BottomNavigationView bottomNavigationView;
    private NavController navController;
    private static View toolbar;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        initBottomTabNav();
        Singleton singleton = new Singleton();
        toolbar = findViewById(R.id.toolbar);
    }

    private void initBottomTabNav() {
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    public static void showTabLayout() {
        if (bottomNavigationView.getVisibility()==View.GONE) {
            bottomNavigationView.setVisibility(VISIBLE);
            toolbar.setVisibility(VISIBLE);
        }
    }

    public static void hideTabLayout() {
        bottomNavigationView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}