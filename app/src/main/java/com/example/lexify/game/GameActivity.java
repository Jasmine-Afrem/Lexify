package com.example.lexify.game;

import com.example.lexify.game.GameManager;
import com.example.lexify.game.WordDetailsActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.lexify.R;
import com.example.lexify.model.Level;
import com.example.lexify.model.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.material.button.MaterialButton;
import android.graphics.Color;

public class GameActivity extends AppCompatActivity {

    private static final int MAX_ROWS_ON_GRID = 6;
    private static final String PREFS_NAME = "LexifyGamePrefs";
    private static final String KEY_CURRENT_LEVEL = "currentLevel";
    
    private int wordLength = 5;

    private GridLayout gridLayoutWord;
    private TextView[][] letterCells;
    private EditText hiddenEditText;
    private Button buttonSubmitGuess;
    private Button buttonHint;
    private Button buttonGiveUp;
    private TextView textViewLevelInfo;
    private TextView textViewClueArea;
    private MaterialButton buttonToggleKeyboard;
    private View keyboardCard;
    private boolean isKeyboardVisible = false;

    private GameManager gameManager;
    private Level currentLevelData;
    private int currentWordIndexInLevel = 0;
    private String currentTargetWord;
    private Word currentWordObject;
    private int currentActiveRowOnGrid = 0;
    private StringBuilder currentGuessBuilder;
    private Map<Integer, String> correctlyGuessedWordsInLevel;

    private ActivityResultLauncher<Intent> wordDetailsLauncher;
    
    private PlayerStats playerStats;
    private long gameStartTime;
    private SharedPreferences gamePrefs;

    private GridLayout hintPreviewGrid;
    private TextView[] hintPreviewCells;
    private boolean[] revealedHintPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize preferences
        gamePrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize game state
        currentGuessBuilder = new StringBuilder();
        correctlyGuessedWordsInLevel = new HashMap<>();
        gameManager = new GameManager();
        playerStats = new PlayerStats(this);
        gameStartTime = System.currentTimeMillis();

        // Initialize UI components
        initializeUIComponents();

        // Set up keyboard toggle and input
        setupKeyboardAndInput();

        // Set up back button handlers
        setupBackButtonHandlers();

        // Set up word details launcher
        setupWordDetailsLauncher();

        // Load saved level or start from beginning
        loadSavedLevelAndStart();
    }

    private void initializeUIComponents() {
        textViewLevelInfo = findViewById(R.id.textViewLevelInfo);
        if (textViewLevelInfo == null) {
            Log.e("GameActivity", "Failed to find textViewLevelInfo");
            showErrorAndFinish("Failed to initialize game components");
            return;
        }

        gridLayoutWord = findViewById(R.id.gridLayoutWord);
        if (gridLayoutWord == null) {
            Log.e("GameActivity", "Failed to find gridLayoutWord");
            showErrorAndFinish("Failed to initialize game components");
            return;
        }

        hiddenEditText = findViewById(R.id.hiddenEditText);
        buttonSubmitGuess = findViewById(R.id.buttonSubmitGuess);
        buttonHint = findViewById(R.id.buttonHint);
        buttonGiveUp = findViewById(R.id.buttonGiveUp);
        textViewClueArea = findViewById(R.id.textViewClueArea);
        buttonToggleKeyboard = findViewById(R.id.buttonToggleKeyboard);
        keyboardCard = findViewById(R.id.keyboardCard);

        hintPreviewGrid = findViewById(R.id.hintPreviewGrid);
        if (hintPreviewGrid == null) {
            Log.e("GameActivity", "Failed to find hintPreviewGrid");
            showErrorAndFinish("Failed to initialize game components");
            return;
        }

        // Set initial button states
        buttonSubmitGuess.setEnabled(true);
        buttonHint.setEnabled(true);
        buttonGiveUp.setEnabled(true);
        buttonToggleKeyboard.setEnabled(true);

        // Set click listeners
        buttonSubmitGuess.setOnClickListener(v -> handleSubmitGuess());
        buttonHint.setOnClickListener(v -> handleHint());
        buttonGiveUp.setOnClickListener(v -> handleGiveUp());
    }

    private void setupKeyboardAndInput() {
        // Set up keyboard toggle
        buttonToggleKeyboard.setOnClickListener(v -> toggleKeyboard());
        keyboardCard.setVisibility(View.GONE);
        keyboardCard.setAlpha(0f);

        // Set up custom keyboard
        setupCustomKeyboard();

        // Set up physical keyboard input
        setupKeyboardInput();

        // Disable system keyboard for hidden EditText
        if (hiddenEditText != null) {
            hiddenEditText.setFocusable(false);
            hiddenEditText.setFocusableInTouchMode(false);
        }

        // Hide system keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = getWindow().getDecorView().getRootView();
        }
        if (imm != null && currentFocus != null && currentFocus.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void setupBackButtonHandlers() {
        ImageButton backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> showExitConfirmationDialog());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        });
    }

    private void setupWordDetailsLauncher() {
        wordDetailsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                proceedToNextLevelOrEndGame();
            });
    }

    private void loadSavedLevelAndStart() {
        // Load saved level or start from beginning
        int savedLevel = gamePrefs.getInt(KEY_CURRENT_LEVEL, 1);
        // Convert from 1-based level number to 0-based index
        gameManager.setCurrentLevel(savedLevel - 1);
        startNewLevel();
    }

    private void showErrorAndFinish(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    private void setupKeyboardInput() {
        View rootView = findViewById(android.R.id.content);
        rootView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                handleSubmitGuess();
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                handleBackspace();
                return true;
            }

            char pressedKey = (char) event.getUnicodeChar();
            if (Character.isLetter(pressedKey)) {
                handleLetterKeyPress(String.valueOf(pressedKey).toUpperCase());
                return true;
            }

            return false;
        });

        // Make sure the root view can receive key events
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit? Your progress will be saved.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Mark remaining words as failed attempts
                    if (currentLevelData != null) {
                        int remainingWords = currentLevelData.getWordsToGuess().size() - currentWordIndexInLevel;
                        for (int i = 0; i < remainingWords; i++) {
                            playerStats.addGuessResult(false);
                        }
                        
                        // Save current level before exiting
                        gamePrefs.edit()
                            .putInt(KEY_CURRENT_LEVEL, currentLevelData.getLevelNumber())
                            .apply();
                    }
                    playerStats.incrementGamesPlayed();
                    
                    // Navigate back to MainActivity
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void startNewLevel() {
        currentLevelData = gameManager.getCurrentLevel();
        System.out.println("GA_DEBUG: startNewLevel - Attempting to load level: " + (currentLevelData != null ? currentLevelData.getLevelNumber() : "NULL"));

        if (currentLevelData == null) {
            showGameEndDialog("Congratulations!", "You've completed all available levels!");
            enableGameControls(false);
            return;
        }

        correctlyGuessedWordsInLevel.clear();
        currentWordIndexInLevel = 0;
        currentActiveRowOnGrid = 0;

        if (currentLevelData.getWordsToGuess().isEmpty()) {
            Toast.makeText(this, "Level " + currentLevelData.getLevelNumber() + " has no words. Skipping.", Toast.LENGTH_SHORT).show();
            proceedToNextLevelOrEndGame();
            return;
        }

        wordLength = currentLevelData.getWordsToGuess().get(0).getWord().length();
        gridLayoutWord.setColumnCount(wordLength);
        int rowsForThisLevel = Math.min(MAX_ROWS_ON_GRID, currentLevelData.getWordsToGuess().size());
        gridLayoutWord.setRowCount(rowsForThisLevel > 0 ? rowsForThisLevel : 1);
        initializeGridUI(rowsForThisLevel > 0 ? rowsForThisLevel : 1);

        prepareForNextWordInLevel();
        System.out.println("GA_DEBUG: startNewLevel - Successfully started Level " + currentLevelData.getLevelNumber());
    }

    private void prepareForNextWordInLevel() {
        if (currentLevelData == null) {
            showErrorAndFinish("Level data is missing.");
            enableGameControls(false);
            return;
        }

        if (currentWordIndexInLevel >= currentLevelData.getWordsToGuess().size()) {
            Toast.makeText(this, "Level " + currentLevelData.getLevelNumber() + " Complete!", Toast.LENGTH_LONG).show();
            enableGameControls(false);
            new Handler(Looper.getMainLooper()).postDelayed(this::proceedToNextLevelOrEndGame, 2000);
            return;
        }

        if (currentActiveRowOnGrid >= gridLayoutWord.getRowCount()) {
            Toast.makeText(this, "Grid full! Level " + currentLevelData.getLevelNumber() + " ended.", Toast.LENGTH_LONG).show();
            enableGameControls(false);
            List<Word> remainingWords = new ArrayList<>();
            for (int i = currentWordIndexInLevel; i < currentLevelData.getWordsToGuess().size(); i++) {
                if (!correctlyGuessedWordsInLevel.containsKey(i)) {
                    remainingWords.add(currentLevelData.getWordsToGuess().get(i));
                }
            }
            if (!remainingWords.isEmpty()) {
                showMissedWordsDialog(remainingWords, "Grid Full! Some words were not displayed.");
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(this::proceedToNextLevelOrEndGame, 2000);
            }
            return;
        }

        currentWordObject = currentLevelData.getWordsToGuess().get(currentWordIndexInLevel);
        currentTargetWord = currentWordObject.getWord().toUpperCase();
        gameManager.resetHintsForNewWord();
        
        // Initialize hint preview
        initializeHintPreview();

        currentGuessBuilder.setLength(0);
        clearInputRow(currentActiveRowOnGrid);

        textViewClueArea.setText(getInitialClue(currentWordObject));
        updateLevelInfoUI();
        
        // Enable all controls and update hint button state
        enableGameControls(true);
        buttonHint.setEnabled(gameManager.getAvailableHints() > 0);
        
        System.out.println("GA_DEBUG: prepareForNextWordInLevel - Word: " + currentTargetWord + " (L:" + currentLevelData.getLevelNumber() + ", W_idx:" + currentWordIndexInLevel + ", GridRow:" + currentActiveRowOnGrid + ")");
    }

    private void initializeHintPreview() {
        hintPreviewGrid.removeAllViews();
        hintPreviewGrid.setColumnCount(currentTargetWord.length());
        hintPreviewGrid.setRowCount(1);
        
        hintPreviewCells = new TextView[currentTargetWord.length()];
        revealedHintPositions = new boolean[currentTargetWord.length()];
        
        int cellSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        
        for (int i = 0; i < currentTargetWord.length(); i++) {
            TextView cell = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSize;
            params.height = cellSize;
            params.setMargins(margin, margin, margin, margin);
            
            cell.setLayoutParams(params);
            cell.setBackgroundResource(R.drawable.grid_cell_bg);
            cell.setTextColor(ContextCompat.getColor(this, R.color.lex_text_on_purple_primary));
            cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            cell.setGravity(Gravity.CENTER);
            cell.setAllCaps(true);
            cell.setText("");
            
            hintPreviewCells[i] = cell;
            hintPreviewGrid.addView(cell);
        }
    }

    private void initializeGridUI(int numRows) {
        gridLayoutWord.removeAllViews();
        letterCells = new TextView[numRows][wordLength];
        int cellSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int textSizeInSp = 28;

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < wordLength; c++) {
                TextView cell = new TextView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(margin, margin, margin, margin);
                cell.setLayoutParams(params);
                cell.setBackgroundResource(R.drawable.grid_cell_bg);
                cell.setTextColor(ContextCompat.getColor(this, R.color.lex_text_on_purple_primary));
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);
                cell.setGravity(Gravity.CENTER);
                cell.setAllCaps(true);
                cell.setText("");
                letterCells[r][c] = cell;
                gridLayoutWord.addView(cell);
            }
        }
    }

    private void clearInputRow(int rowIndex) {
        if (letterCells != null && rowIndex < gridLayoutWord.getRowCount() && rowIndex >= 0) {
            for (int c = 0; c < wordLength; c++) {
                if (letterCells[rowIndex] != null && c < letterCells[rowIndex].length && letterCells[rowIndex][c] != null) {
                    letterCells[rowIndex][c].setText("");
                    setCellBackground(letterCells[rowIndex][c], R.color.game_grid_cell_empty_bg);
                }
            }
        }
    }

    private void setupCustomKeyboard() {
        View keyboardView = findViewById(R.id.customKeyboardLayout);
        if (keyboardView instanceof ViewGroup) {
            setKeyboardListeners((ViewGroup) keyboardView);
        }
    }

    private void setKeyboardListeners(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                setKeyboardListeners((ViewGroup) child);
            } else if (child instanceof Button) {
                Button keyButton = (Button) child;
                if (keyButton.getId() == R.id.buttonBackspaceKeyboard) {
                    keyButton.setOnClickListener(v -> handleBackspace());
                } else if (keyButton.getId() == R.id.buttonEnterKeyboard) {
                    keyButton.setOnClickListener(v -> handleSubmitGuess());
                } else {
                    String keyText = keyButton.getText().toString();
                    keyButton.setOnClickListener(v -> handleLetterKeyPress(keyText));
                }
            }
        }
    }

    private void handleLetterKeyPress(String letter) {
        if (currentActiveRowOnGrid >= gridLayoutWord.getRowCount() || !buttonSubmitGuess.isEnabled()) return;
        if (currentGuessBuilder.length() < wordLength) {
            currentGuessBuilder.append(letter);
            updateGridFromBuilder();
        }
    }

    private void handleBackspace() {
        if (currentActiveRowOnGrid >= gridLayoutWord.getRowCount() || !buttonSubmitGuess.isEnabled()) return;
        if (currentGuessBuilder.length() > 0) {
            currentGuessBuilder.deleteCharAt(currentGuessBuilder.length() - 1);
            updateGridFromBuilder();
        }
    }

    private void updateGridFromBuilder() {
        if (currentActiveRowOnGrid >= gridLayoutWord.getRowCount()) return;
        String currentText = currentGuessBuilder.toString();
        for (int c = 0; c < wordLength; c++) {
            if (letterCells != null && currentActiveRowOnGrid < letterCells.length &&
                    letterCells[currentActiveRowOnGrid] != null && c < letterCells[currentActiveRowOnGrid].length &&
                    letterCells[currentActiveRowOnGrid][c] != null) {
                if (c < currentText.length()) {
                    letterCells[currentActiveRowOnGrid][c].setText(String.valueOf(currentText.charAt(c)));
                } else {
                    letterCells[currentActiveRowOnGrid][c].setText("");
                }
            }
        }
    }

    private void handleSubmitGuess() {
        if (currentActiveRowOnGrid >= gridLayoutWord.getRowCount() || !buttonSubmitGuess.isEnabled() || currentTargetWord == null) return;
        String guess = currentGuessBuilder.toString().toUpperCase();

        if (guess.length() != wordLength) {
            Toast.makeText(this, "Enter a " + wordLength + "-letter word.", Toast.LENGTH_SHORT).show();
            return;
        }

        evaluateGuess(guess, currentActiveRowOnGrid, currentTargetWord);

        if (guess.equals(currentTargetWord)) {
            Toast.makeText(this, "Correct: " + currentTargetWord + "!", Toast.LENGTH_SHORT).show();
            correctlyGuessedWordsInLevel.put(currentWordIndexInLevel, currentTargetWord);
            
            // Track successful guess
            playerStats.incrementWordsGuessed();
            playerStats.addGuessResult(true);
            
            long wordTime = (System.currentTimeMillis() - gameStartTime) / 1000;
            playerStats.addGameTime(wordTime);
            gameStartTime = System.currentTimeMillis();

            currentWordIndexInLevel++;
            currentActiveRowOnGrid++;

            prepareForNextWordInLevel();
        } else {
            // Track failed guess
            playerStats.addGuessResult(false);
            Toast.makeText(this, "Incorrect. Try again or use a hint!", Toast.LENGTH_SHORT).show();
            currentGuessBuilder.setLength(0);
            updateGridFromBuilder();
        }
    }

    private void evaluateGuess(String guess, int rowIndex, String target) {
        if (letterCells == null || rowIndex >= letterCells.length || letterCells[rowIndex] == null || target == null) return;
        boolean[] targetWordLetterUsed = new boolean[target.length()];
        char[] guessChars = guess.toCharArray();
        char[] targetChars = target.toCharArray();

        for (int i = 0; i < target.length(); i++) {
            if (letterCells[rowIndex] != null && i < letterCells[rowIndex].length && letterCells[rowIndex][i] != null) {
                if (i < guessChars.length) {
                    letterCells[rowIndex][i].setText(String.valueOf(guessChars[i]));
                }
                setCellBackground(letterCells[rowIndex][i], R.color.game_grid_cell_incorrect_bg);
            }
        }
        for (int i = 0; i < target.length(); i++) {
            if (i < guessChars.length && letterCells[rowIndex] != null && i < letterCells[rowIndex].length && letterCells[rowIndex][i] != null && guessChars[i] == targetChars[i]) {
                setCellBackground(letterCells[rowIndex][i], R.color.game_grid_cell_correct_pos_bg);
                targetWordLetterUsed[i] = true;
            }
        }
        for (int i = 0; i < target.length(); i++) {
            if (i < guessChars.length && letterCells[rowIndex] != null && i < letterCells[rowIndex].length && letterCells[rowIndex][i] != null && guessChars[i] != targetChars[i]) {
                for (int j = 0; j < target.length(); j++) {
                    if (guessChars[i] == targetChars[j] && !targetWordLetterUsed[j]) {
                        setCellBackground(letterCells[rowIndex][i], R.color.game_grid_cell_correct_letter_bg);
                        targetWordLetterUsed[j] = true;
                        break;
                    }
                }
            }
        }
    }

    private void setCellBackground(TextView cell, int colorResId) {
        if (cell == null) return;
        GradientDrawable cellBackground = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.grid_cell_bg).getConstantState().newDrawable().mutate();
        cellBackground.setColor(ContextCompat.getColor(this, colorResId));
        cell.setBackground(cellBackground);
    }

    private String getInitialClue(Word word) {
        if (word != null && word.getMeanings() != null && !word.getMeanings().isEmpty()) {
            Word.Meaning firstMeaning = word.getMeanings().get(0);
            String definition = firstMeaning.getDefinition();
            int maxLength = Math.min(definition.length(), 100);
            return "Clue (" + firstMeaning.getPartOfSpeech() + "): " + definition.substring(0, maxLength) + (definition.length() > 100 ? "..." : "");
        }
        return "Guess the " + (word != null ? word.getWord().length() : wordLength) + "-letter word!";
    }

    private void updateLevelInfoUI() {
        if (currentLevelData != null) {
            textViewLevelInfo.setText(String.format("Level %d (%d/%d words)",
                    currentLevelData.getLevelNumber(),
                    Math.min(currentWordIndexInLevel + 1, currentLevelData.getWordsToGuess().size()),
                    currentLevelData.getWordsToGuess().size()));
        } else {
            textViewLevelInfo.setText("Game Over");
        }
    }

    private void handleHint() {
        if (!buttonHint.isEnabled() || currentTargetWord == null || currentWordObject == null) return;

        // Check if hints are available
        if (gameManager.getAvailableHints() <= 0) {
            Toast.makeText(this, "No more hints available!", Toast.LENGTH_SHORT).show();
            buttonHint.setEnabled(false);
            return;
        }

        // Find unrevealed positions
        List<Integer> unrevealedPositions = new ArrayList<>();
        for (int i = 0; i < revealedHintPositions.length; i++) {
            if (!revealedHintPositions[i]) {
                unrevealedPositions.add(i);
            }
        }

        if (unrevealedPositions.isEmpty()) {
            Toast.makeText(this, "All letters have been revealed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use a hint from GameManager
        if (gameManager.useHint()) {
            // Randomly select an unrevealed position
            int randomIndex = (int) (Math.random() * unrevealedPositions.size());
            int positionToReveal = unrevealedPositions.get(randomIndex);

            // Reveal the letter
            revealedHintPositions[positionToReveal] = true;
            if (hintPreviewCells != null && positionToReveal < hintPreviewCells.length) {
                TextView cell = hintPreviewCells[positionToReveal];
                cell.setText(String.valueOf(currentTargetWord.charAt(positionToReveal)));
                setCellBackground(cell, R.color.game_grid_cell_correct_pos_bg);
            }

            // Update hint button state
            if (gameManager.getAvailableHints() <= 0) {
                buttonHint.setEnabled(false);
            }
        }
    }

    private void handleGiveUp() {
        Log.d("GA_DEBUG_GiveUp", "handleGiveUp called.");
        if (!buttonGiveUp.isEnabled() || currentLevelData == null) {
            Log.d("GA_DEBUG_GiveUp", "Button not enabled or currentLevelData is null. Returning.");
            return;
        }
        enableGameControls(false);

        // Track give up as failed attempts for remaining words
        int remainingWords = currentLevelData.getWordsToGuess().size() - currentWordIndexInLevel;
        for (int i = 0; i < remainingWords; i++) {
            playerStats.addGuessResult(false);
        }
        
        // Add game time for the current word
        long wordTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        playerStats.addGameTime(wordTime);
        
        // Mark this game as completed since player gave up
        playerStats.incrementGamesPlayed();

        List<Word> wordsInLevel = currentLevelData.getWordsToGuess();
        Log.d("GA_DEBUG_GiveUp", "Number of words in current level: " + wordsInLevel.size());
        Log.d("GA_DEBUG_GiveUp", "GridLayout current rowCount: " + gridLayoutWord.getRowCount());
        Log.d("GA_DEBUG_GiveUp", "letterCells array rows: " + (letterCells != null ? letterCells.length : "null"));

        List<Word> wordsActuallyRevealedNow = new ArrayList<>();
        int gridRowToUse = 0;

        for (int wordIdx = 0; wordIdx < wordsInLevel.size(); wordIdx++) {
            Log.d("GA_DEBUG_GiveUp", "Processing wordIdx: " + wordIdx + ", attempting to use gridRowToUse: " + gridRowToUse);

            if (gridRowToUse >= gridLayoutWord.getRowCount()) {
                Log.w("GA_DEBUG_GiveUp", "Grid is full. gridRowToUse (" + gridRowToUse + ") >= gridLayoutWord.getRowCount() (" + gridLayoutWord.getRowCount() + "). Breaking loop.");
                break;
            }
            if (letterCells == null || gridRowToUse >= letterCells.length || letterCells[gridRowToUse] == null) {
                Log.e("GA_DEBUG_GiveUp", "letterCells array is not properly initialized for row: " + gridRowToUse + ". Breaking loop.");
                break;
            }

            Word wordForThisRow = wordsInLevel.get(wordIdx);
            String wordStrToDisplay = wordForThisRow.getWord().toUpperCase();
            Log.d("GA_DEBUG_GiveUp", "Word to display: " + wordStrToDisplay);

            if (wordStrToDisplay.length() != wordLength) {
                Log.w("GA_DEBUG_GiveUp", "Skipping word '" + wordStrToDisplay + "' due to length mismatch with current grid (" + wordLength + ").");
                for (int c = 0; c < wordLength; c++) {
                    if (c < letterCells[gridRowToUse].length && letterCells[gridRowToUse][c] != null) {
                        letterCells[gridRowToUse][c].setText("?");
                    }
                }
                gridRowToUse++;
                continue;
            }

            for (int c = 0; c < wordLength; c++) {
                if (c < letterCells[gridRowToUse].length && letterCells[gridRowToUse][c] != null) {
                    letterCells[gridRowToUse][c].setText(String.valueOf(wordStrToDisplay.charAt(c)));
                    setCellBackground(letterCells[gridRowToUse][c], R.color.game_grid_cell_correct_pos_bg);
                } else {
                    Log.e("GA_DEBUG_GiveUp", "Error accessing letterCells[" + gridRowToUse + "][" + c + "]");
                }
            }

            if (!correctlyGuessedWordsInLevel.containsValue(wordStrToDisplay)) {
                wordsActuallyRevealedNow.add(wordForThisRow);
            }
            gridRowToUse++;
        }
        Log.d("GA_DEBUG_GiveUp", "Finished filling grid. Final gridRowToUse: " + gridRowToUse);

        if (!wordsActuallyRevealedNow.isEmpty()) {
            Log.d("GA_DEBUG_GiveUp", "Showing missed words dialog for " + wordsActuallyRevealedNow.size() + " words.");
            showMissedWordsDialog(wordsActuallyRevealedNow, "Words Revealed! Want to know their meanings?");
        } else {
            Log.d("GA_DEBUG_GiveUp", "No new words to reveal in dialog. Proceeding to next level.");
            Toast.makeText(this, "All words in the level were already guessed or displayed!", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(this::proceedToNextLevelOrEndGame, 2000);
        }
    }

    private void showMissedWordsDialog(final List<Word> missedOrRevealedWords, String title) {
        if (missedOrRevealedWords.isEmpty()) {
            proceedToNextLevelOrEndGame();
            return;
        }

        final CharSequence[] wordItems = new CharSequence[missedOrRevealedWords.size()];
        for (int i = 0; i < missedOrRevealedWords.size(); i++) {
            wordItems[i] = missedOrRevealedWords.get(i).getWord().toUpperCase();
        }
        final ArrayList<Integer> selectedIndices = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMultiChoiceItems(wordItems, null, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedIndices.add(which);
                    } else if (selectedIndices.contains(which)) {
                        selectedIndices.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton("Show Selected Meanings", (dialog, id) -> {
                    if (selectedIndices.isEmpty()) {
                        Toast.makeText(GameActivity.this, "No words selected. Continuing...", Toast.LENGTH_SHORT).show();
                        proceedToNextLevelOrEndGame();
                    } else {
                        ArrayList<Word> wordsToSendToDetailsActivity = new ArrayList<>();
                        for (int index : selectedIndices) {
                            wordsToSendToDetailsActivity.add(missedOrRevealedWords.get(index));
                        }
                        Intent intent = new Intent(GameActivity.this, WordDetailsActivity.class);
                        intent.putExtra(WordDetailsActivity.EXTRA_WORDS_TO_DISPLAY, wordsToSendToDetailsActivity);
                        wordDetailsLauncher.launch(intent);
                    }
                })
                .setNegativeButton("No, Continue Game", (dialog, id) -> {
                    proceedToNextLevelOrEndGame();
                })
                .setCancelable(false);
        builder.create().show();
    }

    private void proceedToNextLevelOrEndGame() {
        System.out.println("GA_DEBUG: proceedToNextLevelOrEndGame called.");
        boolean hasNextLevel = gameManager.advanceToNextLevel();

        if (hasNextLevel) {
            System.out.println("GA_DEBUG: GameManager advanced. Calling startNewLevel.");
            // Save the new level number
            if (currentLevelData != null) {
                int nextLevelNumber = currentLevelData.getLevelNumber() + 1;
                gamePrefs.edit()
                    .putInt(KEY_CURRENT_LEVEL, nextLevelNumber)
                    .apply();
            }
            startNewLevel();
        } else {
            System.out.println("GA_DEBUG: No more levels from GameManager. Showing end dialog.");
            showGameEndDialog("Congratulations!", "You've completed all available levels!");
            enableGameControls(false);
        }
    }

    private void showGameEndDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Back to Menu", (dialog, which) -> {
                    // Save the final level
                    if (currentLevelData != null) {
                        gamePrefs.edit()
                            .putInt(KEY_CURRENT_LEVEL, currentLevelData.getLevelNumber())
                            .apply();
                    }
                    
                    // Return to MainActivity
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
        enableGameControls(false);
    }

    private void toggleKeyboard() {
        isKeyboardVisible = !isKeyboardVisible;
        
        if (isKeyboardVisible) {
            keyboardCard.setVisibility(View.VISIBLE);
            keyboardCard.animate()
                    .alpha(1f)
                    .translationY(0)
                    .setDuration(300)
                    .start();
        } else {
            keyboardCard.animate()
                    .alpha(0f)
                    .translationY(keyboardCard.getHeight())
                    .setDuration(300)
                    .withEndAction(() -> keyboardCard.setVisibility(View.GONE))
                    .start();
        }
        
        buttonToggleKeyboard.setIconTint(ColorStateList.valueOf(
            ContextCompat.getColor(this, isKeyboardVisible ? 
                R.color.lex_purple_primary : R.color.lex_text_on_surface)));
    }

    private void enableGameControls(boolean enabled) {
        buttonSubmitGuess.setEnabled(enabled);
        buttonHint.setEnabled(enabled);
        buttonGiveUp.setEnabled(enabled);
        buttonToggleKeyboard.setEnabled(enabled);
        View keyboardView = findViewById(R.id.customKeyboardLayout);
        if (keyboardView instanceof ViewGroup) {
            enableDisableViewGroup((ViewGroup) keyboardView, enabled);
        }
    }

    private void enableDisableViewGroup(ViewGroup vg, boolean enabled) {
        if (vg == null) return;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) child, enabled);
            } else {
                child.setEnabled(enabled);
            }
        }
    }
}