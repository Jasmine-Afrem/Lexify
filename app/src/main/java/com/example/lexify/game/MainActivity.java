package com.example.lexify.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.lexify.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private PlayerStats playerStats;
    private NavigationView navigationView;
    private static final String PREFS_NAME = "LexifyGamePrefs";
    private static final String KEY_CURRENT_LEVEL = "currentLevel";
    private SharedPreferences gamePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PlayerStats
        playerStats = new PlayerStats(this);

        // Initialize preferences
        gamePrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set up start game button
        MaterialButton startGameButton = findViewById(R.id.buttonStartGame);
        if (startGameButton != null) {
            startGameButton.setOnClickListener(v -> startGame());
        }

        // Update level display
        updateLevelDisplay();

        // Update player info
        updatePlayerInfo();
        updateStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update level display when returning to MainActivity
        updateLevelDisplay();
        updatePlayerInfo();
        updateStatistics();
    }

    private void updatePlayerInfo() {
        // Get saved player name
        String playerName = gamePrefs.getString("playerName", "Player");
        
        // Update main screen player info
        TextView playerNameView = findViewById(R.id.player_name);
        if (playerNameView != null) {
            playerNameView.setText(playerName);
        }

        // Update navigation drawer header
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView headerPlayerName = headerView.findViewById(R.id.profileName);
            if (headerPlayerName != null) {
                headerPlayerName.setText(playerName);
            }
        }

        // Update level display
        updateLevelDisplay();
    }

    private void updateStatistics() {
        TextView gamesPlayedView = findViewById(R.id.games_played);
        TextView wordsGuessedView = findViewById(R.id.words_guessed);
        TextView avgTimeView = findViewById(R.id.avg_time);
        TextView successRateView = findViewById(R.id.success_rate);

        if (gamesPlayedView != null) {
            gamesPlayedView.setText(String.valueOf(playerStats.getGamesPlayed()));
        }

        if (wordsGuessedView != null) {
            wordsGuessedView.setText(String.valueOf(playerStats.getWordsGuessed()));
        }

        if (avgTimeView != null) {
            float avgTime = playerStats.getAverageTimePerWord();
            avgTimeView.setText(String.format(Locale.getDefault(), "%.1fs", avgTime));
        }

        if (successRateView != null) {
            float successRate = playerStats.getSuccessRate();
            successRateView.setText(String.format(Locale.getDefault(), "%.1f%%", successRate));
        }
    }

    private void updateLevelDisplay() {
        int currentLevel = gamePrefs.getInt(KEY_CURRENT_LEVEL, 1);
        TextView playerLevelView = findViewById(R.id.player_level);
        TextView navHeaderLevel = navigationView.getHeaderView(0).findViewById(R.id.profileLevel);
        
        String levelText = String.format("Level %d", currentLevel);
        if (playerLevelView != null) {
            playerLevelView.setText(levelText);
        }
        if (navHeaderLevel != null) {
            navHeaderLevel.setText(levelText);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            // Already in MainActivity
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_start_game) {
            startGame();
        } else if (itemId == R.id.nav_previous_levels) {
            showPreviousLevels();
        } else if (itemId == R.id.nav_how_to_play) {
            showHowToPlay();
        } else if (itemId == R.id.nav_leaderboard) {
            showLeaderboard();
        } else if (itemId == R.id.nav_settings) {
            openSettings();
        } else if (itemId == R.id.nav_about) {
            showAbout();
        } else if (itemId == R.id.nav_quit) {
            showQuitConfirmationDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showHowToPlay() {
        Toast.makeText(this, "How To Play: Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    private void showLeaderboard() {
        Toast.makeText(this, "Leaderboard: Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    private void showAbout() {
        Toast.makeText(this, "About Lexify: Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    private void showQuitConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.Lexify_AlertDialog)
            .setTitle("Quit Game")
            .setMessage("Are you sure you want to quit Lexify?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Save any necessary data before quitting
                finishAffinity(); // This will close all activities and exit the app
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void showPreviousLevels() {
        Intent intent = new Intent(this, PreviousLevelsActivity.class);
        startActivity(intent);
    }
}