package com.example.lexify.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast; // For How To Play and Leaderboard placeholders
import androidx.appcompat.app.AppCompatActivity;
import com.example.lexify.R;
import com.example.lexify.model.Word;
import com.google.android.material.button.MaterialButton; // For Material Buttons from your layout
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure your activity_main.xml is in res/layout/
        // And the R.layout.activity_main reference is correct.
        setContentView(R.layout.activity_main);

        // Your existing JSON parsing - keep it if it's useful for testing/reference
        String json = "{\n" +
                "  \"word\": \"precipitate\",\n" +
                "  \"meanings\": [\n" +
                "    {\n" +
                "      \"partOfSpeech\": \"verb\",\n" +
                "      \"definition\": \"To cause something to happen suddenly or unexpectedly.\",\n" +
                "      \"examples\": [\n" +
                "        \"The incident precipitated a political crisis.\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"partOfSpeech\": \"adjective\",\n" +
                "      \"definition\": \"Done, made, or acting suddenly or without careful consideration.\",\n" +
                "      \"examples\": [\n" +
                "        \"A precipitate decision that led to disaster.\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Word word = parseWordJson(json);
        System.out.println("Word (from MainActivity test): " + word.getWord());
        // ... (your existing print statements)

        // --- Button Listeners ---
        MaterialButton startGameButton = findViewById(R.id.start_game_button); // Use MaterialButton if your XML uses it
        MaterialButton settingsButton = findViewById(R.id.settings_button);
        MaterialButton howToPlayButton = findViewById(R.id.how_to_play_button);
        MaterialButton leaderboardButton = findViewById(R.id.leaderboard_button);

        if (startGameButton != null) {
            startGameButton.setOnClickListener(v -> {
                // We will launch GameActivity here
                // Assuming GameActivity will be in com.example.lexify.game package as well
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            });
        }

        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                // Assuming SettingsActivity is also in com.example.lexify.game package
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        if (howToPlayButton != null) {
            howToPlayButton.setOnClickListener(v -> {
                // Placeholder for How To Play
                Toast.makeText(MainActivity.this, "How To Play: Coming Soon!", Toast.LENGTH_SHORT).show();
            });
        }

        if (leaderboardButton != null) {
            leaderboardButton.setOnClickListener(v -> {
                // Placeholder for Leaderboard
                Toast.makeText(MainActivity.this, "Leaderboard: Coming Soon!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private Word parseWordJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Word.class);
    }
}