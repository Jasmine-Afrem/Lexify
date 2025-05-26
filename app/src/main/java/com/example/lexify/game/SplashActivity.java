package com.example.lexify.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.lexify.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // Duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install the splash screen
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to transition after SPLASH_DURATION
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish SplashActivity
        }, SPLASH_DURATION);
    }
} 