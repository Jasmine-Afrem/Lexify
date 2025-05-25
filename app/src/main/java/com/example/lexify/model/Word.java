package com.example.lexify.model;

import java.io.Serializable; // <<<< ADD THIS IMPORT
import java.util.List;
// Ensure java.util.ArrayList is used for lists if you want them to be serializable by default.
// java.util.List is an interface; the concrete implementation (like ArrayList) needs to be serializable.
// Gson typically handles this fine when deserializing into List, usually creating ArrayLists.

public class Word implements Serializable { // <<<< IMPLEMENT Serializable HERE
    // It's a good practice to add a serialVersionUID when a class is Serializable,
    // especially if you expect class versions to change over time and need to maintain
    // compatibility with older serialized versions. For simple cases like passing
    // via Intent within the same app version, it's often omitted, but good to know.
    private static final long serialVersionUID = 1L; // Example serialVersionUID

    private String word;
    private List<Meaning> meanings; // ArrayList (which is Serializable) will be used by Gson

    // Getters and Setters (no change to these methods)
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public List<Meaning> getMeanings() { return meanings; }
    public void setMeanings(List<Meaning> meanings) { this.meanings = meanings; }

    public static class Meaning implements Serializable { // <<<< IMPLEMENT Serializable HERE
        private static final long serialVersionUID = 2L; // Example for inner class

        private String partOfSpeech;
        private String definition;
        private List<String> examples;    // ArrayList<String> is Serializable
        private List<String> synonyms;    // ArrayList<String> is Serializable

        // Getters and Setters (no change to these methods)
        public String getPartOfSpeech() { return partOfSpeech; }
        public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
        public String getDefinition() { return definition; }
        public void setDefinition(String definition) { this.definition = definition; }
        public List<String> getExamples() { return examples; }
        public void setExamples(List<String> examples) { this.examples = examples; }
        public List<String> getSynonyms() { return synonyms; }
        public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }
    }
}