package com.example.lexify.game;

import com.example.lexify.model.Level;
import com.example.lexify.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {
    private List<Level> allLevels;
    private int currentLevelIndex;        // 0-based index of the current level being played
    private int currentWordIndexInLevel;  // 0-based index of the current word within that level
    private Word currentWordToGuess;      // The actual Word object for the current word
    private int hintsUsedForCurrentWord;

    public GameManager() {
        loadGameData();
        currentLevelIndex = 0;
        hintsUsedForCurrentWord = 0;
    }

    private Word.Meaning createSimpleMeaning(String pos, String def, String... syns) {
        Word.Meaning m = new Word.Meaning();
        m.setPartOfSpeech(pos);
        m.setDefinition(def);
        if (syns != null && syns.length > 0) {
            m.setSynonyms(Arrays.asList(syns));
        }
        // m.setExamples(Arrays.asList("Example usage...")); // TODO: Add examples
        return m;
    }

    private Word createWord(String wordStr, Word.Meaning... meanings) {
        Word w = new Word();
        w.setWord(wordStr.toUpperCase());
        if (meanings != null) {
            w.setMeanings(Arrays.asList(meanings));
        } else {
            w.setMeanings(new ArrayList<>());
        }
        return w;
    }

    private void loadGameData() {
        allLevels = new ArrayList<>();
        List<Word> words;

        // === Level 1: 4 words, 4 letters ===
        words = new ArrayList<>();
        words.add(createWord("CODE", createSimpleMeaning("noun", "System for secrecy.", "cipher", "key")));
        words.add(createWord("GAME", createSimpleMeaning("noun", "Activity for amusement.", "pastime", "sport")));
        words.add(createWord("WORD", createSimpleMeaning("noun", "Unit of language.", "term", "utterance")));
        words.add(createWord("QUIZ", createSimpleMeaning("noun", "A test of knowledge.", "test", "exam")));
        allLevels.add(new Level(1, words, "Level 1: Easy Peasy"));

        // === Level 2: 4 words, 4 letters ===
        words = new ArrayList<>();
        words.add(createWord("BOOK", createSimpleMeaning("noun", "Set of written sheets.", "tome", "volume")));
        words.add(createWord("READ", createSimpleMeaning("verb", "To look at and comprehend.", "peruse", "scan")));
        words.add(createWord("HOME", createSimpleMeaning("noun", "Place where one lives.", "house", "abode")));
        words.add(createWord("TREE", createSimpleMeaning("noun", "Tall plant with a trunk.", "sapling", "wood")));
        allLevels.add(new Level(2, words, "Level 2: Getting Started"));

        // ... (Include all your other 20 levels here, similar to Level 1 & 2 setup)
        // Example for Level 3
        words = new ArrayList<>();
        words.add(createWord("STAR", createSimpleMeaning("noun", "Celestial body emitting light.", "sun", "luminary")));
        words.add(createWord("MOON", createSimpleMeaning("noun", "Natural satellite of Earth.", "luna", "satellite")));
        words.add(createWord("FISH", createSimpleMeaning("noun", "Cold-blooded aquatic vertebrate.", "aquatic", "marine life")));
        words.add(createWord("BIRD", createSimpleMeaning("noun", "Warm-blooded feathered vertebrate.", "fowl", "avian")));
        allLevels.add(new Level(3, words, "Level 3: Nature Calls"));

        // Make sure all your 22 levels are added to allLevels list
    }

    /**
     * Gets the current Level object based on currentLevelIndex.
     * This method DOES NOT advance the level itself.
     * @return The current Level object, or null if currentLevelIndex is out of bounds or all levels done.
     */
    public Level getCurrentLevel() {
        // System.out.println("GM_DEBUG: getCurrentLevel() called. currentLevelIndex: " + currentLevelIndex);
        if (currentLevelIndex >= 0 && currentLevelIndex < allLevels.size()) {
            return allLevels.get(currentLevelIndex);
        }
        // System.out.println("GM_DEBUG: getCurrentLevel() returning null.");
        return null; // Signifies no current level (either before start or after all levels completed)
    }

    /**
     * Advances to the next word in the game, moving to the next level if necessary.
     * This is the primary method for progressing through words and levels.
     * Sets currentWordToGuess internally.
     * @return The next Word object to guess, or null if all levels are completed.
     */
    public Word getNextWord() {
        // System.out.println("GM_DEBUG: getNextWord() ENTRY. currentLevelIndex: " + currentLevelIndex + ", currentWordIndexInLevel: " + currentWordIndexInLevel);
        hintsUsedForCurrentWord = 0;

        currentWordIndexInLevel++; // Try to advance word in current level

        if (currentLevelIndex < allLevels.size()) {
            Level actualCurrentLevelObject = allLevels.get(currentLevelIndex);
            // System.out.println("GM_DEBUG: getNextWord() - Current Level " + actualCurrentLevelObject.getLevelNumber() + " has " + actualCurrentLevelObject.getWordsToGuess().size() + " words.");

            if (currentWordIndexInLevel < actualCurrentLevelObject.getWordsToGuess().size()) {
                currentWordToGuess = actualCurrentLevelObject.getWordsToGuess().get(currentWordIndexInLevel);
                // System.out.println("GM_DEBUG: getNextWord() - SUCCESS in same level. Word: " + currentWordToGuess.getWord() + " (L:" + (currentLevelIndex+1) + ", W:" + (currentWordIndexInLevel+1) + ")");
                return currentWordToGuess;
            } else {
                // End of current level, try to advance to next level
                // System.out.println("GM_DEBUG: getNextWord() - End of Level " + (currentLevelIndex + 1) + ". Attempting to advance level index.");
                currentLevelIndex++;
                if (currentLevelIndex < allLevels.size()) {
                    currentWordIndexInLevel = 0; // Reset for new level
                    currentWordToGuess = allLevels.get(currentLevelIndex).getWordsToGuess().get(currentWordIndexInLevel);
                    // System.out.println("GM_DEBUG: getNextWord() - SUCCESS moved to new Level " + (currentLevelIndex + 1) + ". Word: " + currentWordToGuess.getWord() + " (L:" + (currentLevelIndex+1) + ", W:" + (currentWordIndexInLevel+1) + ")");
                    return currentWordToGuess;
                } else {
                    currentWordToGuess = null; // All levels completed
                    // System.out.println("GM_DEBUG: getNextWord() - NO MORE LEVELS. currentLevelIndex is now " + currentLevelIndex);
                    return null;
                }
            }
        } else {
            currentWordToGuess = null; // Already past the last level
            // System.out.println("GM_DEBUG: getNextWord() - NO MORE LEVELS (initial check). currentLevelIndex is " + currentLevelIndex);
            return null;
        }
    }

    public Word getCurrentWordToGuess() {
        // This should ideally return the word that getNextWord() last set.
        return currentWordToGuess;
    }

    public String getJumbledWord() { // Not used in Wordle grid, but keeping
        if (currentWordToGuess == null) return "";
        String word = currentWordToGuess.getWord();
        List<Character> characters = new ArrayList<>();
        for (char c : word.toUpperCase().toCharArray()) { characters.add(c); }
        Collections.shuffle(characters, new Random(System.nanoTime()));
        StringBuilder jumbled = new StringBuilder();
        for (char c : characters) { jumbled.append(c); }
        if (jumbled.toString().equals(word.toUpperCase()) && word.length() > 1) {
            return getJumbledWord();
        }
        return jumbled.toString();
    }

    public boolean checkGuess(String guess) { // Not used directly by GameActivity if GameActivity does the check
        if (currentWordToGuess == null || guess == null) return false;
        return currentWordToGuess.getWord().equalsIgnoreCase(guess.trim());
    }

// In GameManager.java (MODIFIED METHOD SIGNATURE AND LOGIC)
    /**
     * Provides a hint for the given Word object.
     * GameActivity should pass the current word it is displaying.
     * @param currentWordObject The Word object currently being guessed.
     * @return A hint string.
     */
    public String getHint(Word currentWordObject) { // Parameter changed
        if (currentWordObject == null) return "No word loaded for hints.";

        String actualWord = currentWordObject.getWord();
        hintsUsedForCurrentWord++; // Increment hints used for *this specific word*

        // Reveal letters one by one
        if (hintsUsedForCurrentWord <= actualWord.length()) {
            StringBuilder hintText = new StringBuilder("Hint: The word is ");
            for (int i = 0; i < actualWord.length(); i++) {
                if (i < hintsUsedForCurrentWord) {
                    hintText.append(actualWord.charAt(i));
                } else {
                    hintText.append("_"); // Use underscore for unrevealed letters
                }
                if (i < actualWord.length() -1) hintText.append(" ");
            }
            return hintText.toString();
        } else if (currentWordObject.getMeanings() != null && !currentWordObject.getMeanings().isEmpty()) {
            Word.Meaning firstMeaning = currentWordObject.getMeanings().get(0);
            // Example: give part of speech after all letters revealed
            if (hintsUsedForCurrentWord == actualWord.length() + 1) {
                return "Hint: Part of speech is " + firstMeaning.getPartOfSpeech() + ".";
            }
            // Example: give a synonym
            if (hintsUsedForCurrentWord == actualWord.length() + 2 &&
                    firstMeaning.getSynonyms() != null &&
                    !firstMeaning.getSynonyms().isEmpty()) {
                return "Hint: A synonym is '" + firstMeaning.getSynonyms().get(0) + "'.";
            }
            // You can add more hint types here, e.g., first letter of definition
        }
        return "No more hints for this word.";
    }

    /**
     * Returns the 1-based number of the current word being attempted within its level.
     */
    public int getCurrentWordNumberInLevelForDisplay() {
        return currentWordIndexInLevel + 1;
    }

    /**
     * Checks if the GameManager's internal currentWordIndexInLevel is at the last word
     * of the GameManager's internal currentLevelData.
     */
    public boolean isLastWordInCurrentManagerLevel() {
        if (currentLevelIndex < allLevels.size()) {
            Level level = allLevels.get(currentLevelIndex);
            return currentWordIndexInLevel >= level.getWordsToGuess().size() - 1;
        }
        return true; // No current level or past all levels
    }

    /**
     * Advances the game to the next level.
     * @return true if successfully advanced to a new valid level, false if all levels are completed.
     */
    public boolean advanceToNextLevel() {
        if (currentLevelIndex < allLevels.size() - 1) {
            currentLevelIndex++;
            System.out.println("GM_DEBUG: Advanced to level index: " + currentLevelIndex);
            return true; // Advanced to a new level
        }
        // Already at or past the last level
        currentLevelIndex = allLevels.size(); // Ensure it's marked as definitely past all levels
        System.out.println("GM_DEBUG: No more levels. currentLevelIndex is now: " + currentLevelIndex);
        return false; // No more levels to advance to
    }

    /**
     * Resets the hint counter for a new word.
     * Should be called by GameActivity when it prepares a new word for guessing.
     */
    public void resetHintsForNewWord() {
        hintsUsedForCurrentWord = 0;
    }

    /**
     * Checks if the GameManager's internal currentLevelIndex has moved past all defined levels.
     */
    public boolean areAllGameManagerLevelsCompleted() {
        return currentLevelIndex >= allLevels.size();
    }
}