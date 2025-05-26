package com.example.lexify.game;

import android.content.Context;
import android.content.SharedPreferences;

public class PlayerStats {
    private static final String PREFS_NAME = "LexifyPlayerStats";
    private static final String KEY_PLAYER_NAME = "playerName";
    private static final String KEY_PLAYER_LEVEL = "playerLevel";
    private static final String KEY_GAMES_PLAYED = "gamesPlayed";
    private static final String KEY_WORDS_GUESSED = "wordsGuessed";
    private static final String KEY_TOTAL_TIME = "totalTime";
    private static final String KEY_SUCCESSFUL_GUESSES = "successfulGuesses";
    private static final String KEY_TOTAL_GUESSES = "totalGuesses";

    private final SharedPreferences prefs;

    public PlayerStats(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setPlayerName(String name) {
        prefs.edit().putString(KEY_PLAYER_NAME, name).apply();
    }

    public String getPlayerName() {
        return prefs.getString(KEY_PLAYER_NAME, "Player");
    }

    public void setPlayerLevel(int level) {
        prefs.edit().putInt(KEY_PLAYER_LEVEL, level).apply();
    }

    public int getPlayerLevel() {
        return prefs.getInt(KEY_PLAYER_LEVEL, 1);
    }

    public void incrementGamesPlayed() {
        int current = getGamesPlayed();
        prefs.edit().putInt(KEY_GAMES_PLAYED, current + 1).apply();
    }

    public int getGamesPlayed() {
        return prefs.getInt(KEY_GAMES_PLAYED, 0);
    }

    public void incrementWordsGuessed() {
        int current = getWordsGuessed();
        prefs.edit().putInt(KEY_WORDS_GUESSED, current + 1).apply();
    }

    public int getWordsGuessed() {
        return prefs.getInt(KEY_WORDS_GUESSED, 0);
    }

    public void addGameTime(long timeInSeconds) {
        long current = getTotalTime();
        prefs.edit().putLong(KEY_TOTAL_TIME, current + timeInSeconds).apply();
    }

    public long getTotalTime() {
        return prefs.getLong(KEY_TOTAL_TIME, 0);
    }

    public float getAverageTimePerWord() {
        int words = getWordsGuessed();
        if (words == 0) return 0;
        return (float) getTotalTime() / words;
    }

    public void addGuessResult(boolean successful) {
        SharedPreferences.Editor editor = prefs.edit();
        if (successful) {
            editor.putInt(KEY_SUCCESSFUL_GUESSES, getSuccessfulGuesses() + 1);
        }
        editor.putInt(KEY_TOTAL_GUESSES, getTotalGuesses() + 1);
        editor.apply();
    }

    public int getSuccessfulGuesses() {
        return prefs.getInt(KEY_SUCCESSFUL_GUESSES, 0);
    }

    public int getTotalGuesses() {
        return prefs.getInt(KEY_TOTAL_GUESSES, 0);
    }

    public float getSuccessRate() {
        int total = getTotalGuesses();
        if (total == 0) return 0;
        return ((float) getSuccessfulGuesses() / total) * 100;
    }

    public void resetStats() {
        prefs.edit()
            .putInt(KEY_GAMES_PLAYED, 0)
            .putInt(KEY_WORDS_GUESSED, 0)
            .putLong(KEY_TOTAL_TIME, 0)
            .putInt(KEY_SUCCESSFUL_GUESSES, 0)
            .putInt(KEY_TOTAL_GUESSES, 0)
            .apply();
    }
} 