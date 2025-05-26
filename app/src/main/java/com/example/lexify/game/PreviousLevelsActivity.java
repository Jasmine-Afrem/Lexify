package com.example.lexify.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import android.util.TypedValue;
import android.util.DisplayMetrics;
import com.example.lexify.R;

public class PreviousLevelsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "LexifyGamePrefs";
    private static final String KEY_CURRENT_LEVEL = "currentLevel";
    private static final int COLUMNS = 3; // Fixed number of columns
    private static final int GRID_SPACING_DP = 16; // Spacing between cells
    private static final int CELL_SIZE_DP = 100; // Fixed cell size

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_levels);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
            
            // Set up centered title
            TextView titleText = new TextView(this);
            titleText.setText("Previous Levels");
            titleText.setTextColor(ContextCompat.getColor(this, R.color.lex_surface_color));
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            titleText.setGravity(Gravity.CENTER);
            
            MaterialToolbar.LayoutParams params = new MaterialToolbar.LayoutParams(
                MaterialToolbar.LayoutParams.WRAP_CONTENT,
                MaterialToolbar.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER;
            titleText.setLayoutParams(params);
            
            toolbar.addView(titleText);
        }

        // Get the current level from preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentLevel = prefs.getInt(KEY_CURRENT_LEVEL, 1);

        // Set up the grid of levels
        setupLevelsGrid(currentLevel);
    }

    private void setupLevelsGrid(int currentLevel) {
        GridLayout gridLayout = findViewById(R.id.levelsGrid);
        if (gridLayout == null) return;

        // Calculate cell size and spacing in pixels
        int cellSizePx = dpToPx(CELL_SIZE_DP);
        int gridSpacingPx = dpToPx(GRID_SPACING_DP);

        gridLayout.setColumnCount(COLUMNS);
        int rows = (currentLevel - 1) / COLUMNS + 1;
        gridLayout.setRowCount(rows);

        // Create cells for each level
        for (int level = 1; level <= currentLevel; level++) {
            MaterialCardView cardView = new MaterialCardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSizePx;
            params.height = cellSizePx;
            params.setMargins(gridSpacingPx, gridSpacingPx, gridSpacingPx, gridSpacingPx);
            
            int row = (level - 1) / COLUMNS;
            int col = (level - 1) % COLUMNS;
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(col);
            cardView.setLayoutParams(params);

            // Style the card
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.lex_purple_primary));
            cardView.setRadius(dpToPx(16));
            cardView.setCardElevation(dpToPx(4));
            
            // Add level number text
            TextView levelText = new TextView(this);
            levelText.setText(String.valueOf(level));
            levelText.setTextColor(ContextCompat.getColor(this, R.color.lex_surface_color));
            levelText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            levelText.setGravity(Gravity.CENTER);
            
            MaterialCardView.LayoutParams textParams = new MaterialCardView.LayoutParams(
                MaterialCardView.LayoutParams.MATCH_PARENT,
                MaterialCardView.LayoutParams.MATCH_PARENT
            );
            levelText.setLayoutParams(textParams);
            
            cardView.addView(levelText);

            // Set click listener
            final int selectedLevel = level;
            cardView.setOnClickListener(v -> startSelectedLevel(selectedLevel));

            gridLayout.addView(cardView);
        }
    }

    private void startSelectedLevel(int level) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("SELECTED_LEVEL", level);
        intent.putExtra("IS_REPLAY", true);
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
} 