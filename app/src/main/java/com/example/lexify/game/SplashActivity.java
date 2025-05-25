package com.example.lexify.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen; // Still use this for proper entry handling

import com.example.lexify.R; // You need this for R.layout.activity_splash

public class SplashActivity extends AppCompatActivity {

    private static final int YOUR_SPLASH_XML_DURATION = 2500; // How long to show YOUR XML layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install the splash screen. This handles the transition from the (now minimal)
        // system splash to your activity.
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // *** SET YOUR XML LAYOUT AS THE CONTENT VIEW ***
        setContentView(R.layout.activity_splash); // THIS IS YOUR DESIGNED SCREEN

        // Handler to transition after YOUR_SPLASH_XML_DURATION
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish SplashActivity
        }, YOUR_SPLASH_XML_DURATION);
    }
}