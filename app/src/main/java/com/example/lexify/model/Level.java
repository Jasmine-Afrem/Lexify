package com.example.lexify.model;

import java.util.List;

public class Level {
    private int levelNumber;
    private List<Word> wordsToGuess; // List of words for this level
    private String levelDescription; // e.g., "Level 1: Common 4-letter words"
    private int wordsInLevel; // Total words for this level

    public Level(int levelNumber, List<Word> wordsToGuess, String levelDescription) {
        this.levelNumber = levelNumber;
        this.wordsToGuess = wordsToGuess;
        this.levelDescription = levelDescription;
        this.wordsInLevel = wordsToGuess.size();
    }

    // Getters
    public int getLevelNumber() { return levelNumber; }
    public List<Word> getWordsToGuess() { return wordsToGuess; }
    public String getLevelDescription() { return levelDescription; }
    public int getWordsInLevelCount() { return wordsInLevel; }
}