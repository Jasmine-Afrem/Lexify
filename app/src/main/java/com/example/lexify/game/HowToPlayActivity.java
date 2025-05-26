package com.example.lexify.game;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lexify.R;
import com.google.android.material.appbar.MaterialToolbar;

public class HowToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
} 