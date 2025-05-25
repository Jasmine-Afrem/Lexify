    package com.example.lexify.game;

    import com.example.lexify.game.GameManager;
    import com.example.lexify.game.WordDetailsActivity;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.drawable.GradientDrawable;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    // Only import InputFilter if hiddenEditText might be used with it directly
    // import android.text.InputFilter;
    // import android.text.SpannableString;
    // import android.text.Spanned;
    // import android.text.TextUtils;
    import android.util.Log;
    import android.util.TypedValue;
    import android.view.Gravity;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.GridLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import com.example.lexify.R;
    import com.example.lexify.model.Level;
    import com.example.lexify.model.Word;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class GameActivity extends AppCompatActivity {

        private static final int MAX_ROWS_ON_GRID = 6;
        private int wordLength = 5;

        private GridLayout gridLayoutWord;
        private TextView[][] letterCells;
        private EditText hiddenEditText;
        private Button buttonSubmitGuess;
        private Button buttonHint;
        private Button buttonGiveUp;
        private TextView textViewLevelInfo;
        private TextView textViewClueArea;

        private GameManager gameManager;
        private Level currentLevelData;
        private int currentWordIndexInLevel = 0;
        private String currentTargetWord;
        private Word currentWordObject;
        private int currentActiveRowOnGrid = 0;
        private StringBuilder currentGuessBuilder;
        private Map<Integer, String> correctlyGuessedWordsInLevel;

        private ActivityResultLauncher<Intent> wordDetailsLauncher;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game);

            currentGuessBuilder = new StringBuilder();
            correctlyGuessedWordsInLevel = new HashMap<>();

            textViewLevelInfo = findViewById(R.id.textViewLevelInfo);
            gridLayoutWord = findViewById(R.id.gridLayoutWord);
            hiddenEditText = findViewById(R.id.hiddenEditText);
            buttonSubmitGuess = findViewById(R.id.buttonSubmitGuess);
            buttonHint = findViewById(R.id.buttonHint);
            buttonGiveUp = findViewById(R.id.buttonGiveUp);
            textViewClueArea = findViewById(R.id.textViewClueArea);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocus = getCurrentFocus();
            if (currentFocus == null) {
                currentFocus = getWindow().getDecorView().getRootView();
            }
            if (imm != null && currentFocus != null && currentFocus.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            if (hiddenEditText != null) {
                hiddenEditText.setFocusable(false);
                hiddenEditText.setFocusableInTouchMode(false);
            }

            wordDetailsLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // This callback is executed when WordDetailsActivity finishes.
                        proceedToNextLevelOrEndGame(); // MODIFIED: Call the new progression method
                    });

            gameManager = new GameManager(); // GameManager's currentLevelIndex is 0 initially
            startNewLevel(); // This will load data for level at index 0
            setupCustomKeyboard();

            buttonSubmitGuess.setOnClickListener(v -> handleSubmitGuess());
            buttonHint.setOnClickListener(v -> handleHint());
            buttonGiveUp.setOnClickListener(v -> handleGiveUp());
        }

        // In GameActivity.java
        private void startNewLevel() {
            // Fetches the current level data based on GameManager's internal state.
            currentLevelData = gameManager.getCurrentLevel();
            System.out.println("GA_DEBUG: startNewLevel - Attempting to load level: " + (currentLevelData != null ? currentLevelData.getLevelNumber() : "NULL"));


            if (currentLevelData == null) {
                // No more levels available from GameManager
                showGameEndDialog("Congratulations!", "You've completed all available levels!");
                enableGameControls(false);
                return;
            }

            // Reset for the new level
            correctlyGuessedWordsInLevel.clear();
            currentWordIndexInLevel = 0; // Start with the first word of this new level
            currentActiveRowOnGrid = 0;   // Start displaying words from the first grid row

            if (currentLevelData.getWordsToGuess().isEmpty()) {
                Toast.makeText(this, "Level " + currentLevelData.getLevelNumber() + " has no words. Skipping.", Toast.LENGTH_SHORT).show();
                // This level is empty, so try to proceed to the next one immediately.
                proceedToNextLevelOrEndGame(); // Use the new progression method
                return;
            }

            // Configure grid based on the first word
            wordLength = currentLevelData.getWordsToGuess().get(0).getWord().length();
            gridLayoutWord.setColumnCount(wordLength);
            int rowsForThisLevel = Math.min(MAX_ROWS_ON_GRID, currentLevelData.getWordsToGuess().size());
            gridLayoutWord.setRowCount(rowsForThisLevel > 0 ? rowsForThisLevel : 1);
            initializeGridUI(rowsForThisLevel > 0 ? rowsForThisLevel : 1);

            prepareForNextWordInLevel(); // Prepare for the first word of this new level
            System.out.println("GA_DEBUG: startNewLevel - Successfully started Level " + currentLevelData.getLevelNumber());
        }

        // In GameActivity.java
        private void prepareForNextWordInLevel() {
            if (currentLevelData == null) {
                showGameEndDialog("Error", "Level data is missing.");
                enableGameControls(false);
                return;
            }

            // Check if all words in the currentLevelData have been processed
            if (currentWordIndexInLevel >= currentLevelData.getWordsToGuess().size()) {
                Toast.makeText(this, "Level " + currentLevelData.getLevelNumber() + " Complete!", Toast.LENGTH_LONG).show();
                enableGameControls(false);
                // Level finished, now trigger progression to the next one
                new Handler(Looper.getMainLooper()).postDelayed(this::proceedToNextLevelOrEndGame, 2000); // Use new method
                return;
            }

            // Check if the grid is full
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
                    // showMissedWordsDialog will eventually lead to proceedToNextLevelOrEndGame
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(this::proceedToNextLevelOrEndGame, 2000); // Use new method
                }
                return;
            }

            // Set up for the current word to guess
            currentWordObject = currentLevelData.getWordsToGuess().get(currentWordIndexInLevel);
            currentTargetWord = currentWordObject.getWord().toUpperCase();
            gameManager.resetHintsForNewWord(); // ADD THIS: Reset hint counter for this new word

            currentGuessBuilder.setLength(0);
            clearInputRow(currentActiveRowOnGrid); // Clears the row for the new word

            textViewClueArea.setText(getInitialClue(currentWordObject));
            updateLevelInfoUI();
            enableGameControls(true);
            System.out.println("GA_DEBUG: prepareForNextWordInLevel - Word: " + currentTargetWord + " (L:" + currentLevelData.getLevelNumber() + ", W_idx:" + currentWordIndexInLevel + ", GridRow:" + currentActiveRowOnGrid + ")");
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
            if (letterCells != null && rowIndex < gridLayoutWord.getRowCount() && rowIndex >= 0) { // Use gridLayoutWord.getRowCount()
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
                    String keyText = keyButton.getText().toString();
                    if ("DEL".equals(keyText)) {
                        keyButton.setOnClickListener(v -> handleBackspace());
                    } else if ("ENTER".equals(keyText)) {
                        keyButton.setOnClickListener(v -> handleSubmitGuess());
                    } else {
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

                // Move to the next word in the level
                currentWordIndexInLevel++; // Advance to next word in the *current level's list*
                currentActiveRowOnGrid++;   // Use the next row on the grid for the next word

                prepareForNextWordInLevel(); // This will either setup next word or detect level completion
            } else {
                Toast.makeText(this, "Incorrect. Try again or use a hint!", Toast.LENGTH_SHORT).show();
                currentGuessBuilder.setLength(0);
                updateGridFromBuilder(); // To clear displayed text for re-typing
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

        // In GameActivity.java
        private void handleHint() {
            if (!buttonHint.isEnabled() || currentTargetWord == null || currentWordObject == null) return; // ADD check for currentWordObject
            // Pass the current Word object to the GameManager's hint method
            String hintMessage = gameManager.getHint(currentWordObject); // MODIFIED
            if (textViewClueArea.getText().toString().toLowerCase().startsWith("clue (") ||
                    textViewClueArea.getText().toString().toLowerCase().startsWith("hint:")) { // Check if it's already a hint
                textViewClueArea.setText(hintMessage); // Replace initial clue or previous hint
            } else {
                textViewClueArea.append("\n" + hintMessage); // Append new hint
            }
            Toast.makeText(this, hintMessage, Toast.LENGTH_LONG).show();
        }

        // In GameActivity.java
        private void handleGiveUp() {
            Log.d("GA_DEBUG_GiveUp", "handleGiveUp called.");
            if (!buttonGiveUp.isEnabled() || currentLevelData == null) {
                Log.d("GA_DEBUG_GiveUp", "Button not enabled or currentLevelData is null. Returning.");
                return;
            }
            enableGameControls(false);

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
                    gridRowToUse++; // Still consume the row, but mark as skipped
                    continue;
                }

                // Fill the grid row
                for (int c = 0; c < wordLength; c++) {
                    if (c < letterCells[gridRowToUse].length && letterCells[gridRowToUse][c] != null) {
                        letterCells[gridRowToUse][c].setText(String.valueOf(wordStrToDisplay.charAt(c)));
                        setCellBackground(letterCells[gridRowToUse][c], R.color.game_grid_cell_correct_pos_bg); // Or game_grid_cell_revealed_bg
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
                    .setNegativeButton("No, Continue Game", (dialog, id) -> { // Keep only ONE negative button
                        // User explicitly chose to skip viewing meanings and continue.
                        proceedToNextLevelOrEndGame();
                    })
                    .setCancelable(false);
            builder.create().show();
        }

        private void proceedToNextLevelOrEndGame() {
            System.out.println("GA_DEBUG: proceedToNextLevelOrEndGame called.");
            // 1. Tell GameManager to advance its internal level pointer.
            boolean hasNextLevel = gameManager.advanceToNextLevel();

            if (hasNextLevel) {
                // GameManager is now pointing to the next level.
                // startNewLevel() will call gameManager.getCurrentLevel() to get this new level's data.
                System.out.println("GA_DEBUG: GameManager advanced. Calling startNewLevel.");
                startNewLevel();
            } else {
                // GameManager indicated no more levels.
                System.out.println("GA_DEBUG: No more levels from GameManager. Showing end dialog.");
                showGameEndDialog("Congratulations!", "You've completed all available levels!");
                enableGameControls(false);
            }
        }

        private void showGameEndDialog(String title, String message) {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Back to Menu", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
            enableGameControls(false);
        }

        private void enableGameControls(boolean enabled) {
            buttonSubmitGuess.setEnabled(enabled);
            buttonHint.setEnabled(enabled);
            buttonGiveUp.setEnabled(enabled);
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