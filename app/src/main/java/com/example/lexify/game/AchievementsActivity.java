package com.example.lexify.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lexify.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView achievementsRecyclerView;
    private AchievementsAdapter achievementsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize RecyclerView
        achievementsRecyclerView = findViewById(R.id.achievementsRecyclerView);
        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Create and set adapter
        List<Achievement> achievements = createAchievements();
        achievementsAdapter = new AchievementsAdapter(achievements);
        achievementsRecyclerView.setAdapter(achievementsAdapter);
    }

    private List<Achievement> createAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        
        // Unlocked achievements (for demo)
        achievements.add(new Achievement(
            "First Word",
            "Guess your first word correctly",
            R.drawable.ic_achievement,
            true  // unlocked
        ));
        
        achievements.add(new Achievement(
            "Word Master",
            "Guess 5 words correctly",
            R.drawable.ic_achievement,
            true  // unlocked
        ));
        
        // Locked achievements
        achievements.add(new Achievement(
            "Speed Demon",
            "Guess a word in under 30 seconds",
            R.drawable.ic_achievement,
            false  // locked
        ));
        
        achievements.add(new Achievement(
            "Perfect Round",
            "Complete a level without any mistakes",
            R.drawable.ic_achievement,
            false  // locked
        ));
        
        achievements.add(new Achievement(
            "Persistent Player",
            "Play 5 games in a row",
            R.drawable.ic_achievement,
            false  // locked
        ));

        achievements.add(new Achievement(
            "Word Wizard",
            "Guess 20 words correctly",
            R.drawable.ic_achievement,
            false  // locked
        ));

        achievements.add(new Achievement(
            "Quick Thinker",
            "Complete a level in under 2 minutes",
            R.drawable.ic_achievement,
            false  // locked
        ));

        achievements.add(new Achievement(
            "Lexify Master",
            "Reach level 10",
            R.drawable.ic_achievement,
            false  // locked
        ));

        return achievements;
    }

    // Achievement data class
    public static class Achievement {
        String title;
        String description;
        int iconResId;
        boolean isUnlocked;

        public Achievement(String title, String description, int iconResId, boolean isUnlocked) {
            this.title = title;
            this.description = description;
            this.iconResId = iconResId;
            this.isUnlocked = isUnlocked;
        }
    }
} 