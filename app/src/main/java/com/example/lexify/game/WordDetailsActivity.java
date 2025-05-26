package com.example.lexify.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lexify.R;
import com.example.lexify.model.Word;
import java.util.ArrayList;
import java.util.List;

public class WordDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_WORDS_TO_DISPLAY = "extra_words_to_display";
    private static final String TAG = "WordDetails_DEBUG";

    private ArrayList<Word> wordsToDisplay;
    private WordDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);
        Log.d(TAG, "onCreate: Method started.");

        try {
            RecyclerView recyclerView = findViewById(R.id.recyclerViewWordDetails);
            if (recyclerView == null) {
                Log.e(TAG, "onCreate: FATAL - recyclerViewWordDetails is NULL. Check your layout XML ID.");
                Toast.makeText(this, "Error: Details view not found.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Log.d(TAG, "onCreate: recyclerViewWordDetails found.");

            Button buttonDone = findViewById(R.id.buttonDoneViewingDetails);
            if (buttonDone == null) {
                Log.e(TAG, "onCreate: FATAL - buttonDoneViewingDetails is NULL. Check your layout XML ID.");
                Toast.makeText(this, "Error: Done button not found.", Toast.LENGTH_LONG).show();
                // Decide if this is fatal. If so, finish(); return;
            } else {
                Log.d(TAG, "onCreate: buttonDoneViewingDetails found.");
                buttonDone.setOnClickListener(v -> {
                    Log.d(TAG, "Done button clicked. Finishing WordDetailsActivity.");
                    setResult(RESULT_OK);
                    finish();
                });
            }

            Log.d(TAG, "onCreate: Attempting to get Intent extras.");
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(EXTRA_WORDS_TO_DISPLAY)) {
                wordsToDisplay = (ArrayList<Word>) intent.getSerializableExtra(EXTRA_WORDS_TO_DISPLAY);
                if (wordsToDisplay != null) {
                    Log.d(TAG, "onCreate: Successfully retrieved " + wordsToDisplay.size() + " words from Intent.");
                } else {
                    Log.w(TAG, "onCreate: EXTRA_WORDS_TO_DISPLAY was present, but getSerializableExtra returned null. Word/Meaning classes Serializable?");
                    wordsToDisplay = new ArrayList<>(); // Avoid NullPointerException later
                }
            } else {
                Log.w(TAG, "onCreate: Intent was null or did not contain EXTRA_WORDS_TO_DISPLAY. Initializing empty list.");
                wordsToDisplay = new ArrayList<>(); // Avoid NullPointerException later
            }

            Log.d(TAG, "onCreate: Initializing WordDetailsAdapter with " + wordsToDisplay.size() + " words.");
            adapter = new WordDetailsAdapter(wordsToDisplay); // Initialize the class member adapter

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "onCreate: LinearLayoutManager set.");

            recyclerView.setAdapter(adapter);
            Log.d(TAG, "onCreate: Adapter set to RecyclerView.");

            Log.d(TAG, "onCreate: Setup complete.");

        } catch (Exception e) {
            Log.e(TAG, "onCreate: CRASHED during setup!", e);
            Toast.makeText(this, "Error loading word details: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    // Adapter for the RecyclerView - MAKE SURE IT'S ALSO ROBUST
    private static class WordDetailsAdapter extends RecyclerView.Adapter<WordDetailsAdapter.ViewHolder> {
        private List<Word> wordList;
        private static final String ADAPTER_TAG = "WordDetailsAdapter_DEBUG";

        @SuppressLint("LongLogTag")
        public WordDetailsAdapter(List<Word> wordList) {
            // Ensure wordList is never null for the adapter
            this.wordList = (wordList != null) ? wordList : new ArrayList<>();
            Log.d(ADAPTER_TAG, "Adapter created with " + this.wordList.size() + " words.");
        }

        @SuppressLint("LongLogTag")
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(ADAPTER_TAG, "onCreateViewHolder called");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_detail, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(ADAPTER_TAG, "onBindViewHolder called for position: " + position);
            if (position >= wordList.size()) {
                Log.e(ADAPTER_TAG, "onBindViewHolder: Position " + position + " is out of bounds for wordList size " + wordList.size());
                return; // Avoid crash
            }
            Word word = wordList.get(position);
            if (word == null) {
                Log.e(ADAPTER_TAG, "onBindViewHolder: Word at position " + position + " is null!");
                holder.textViewWord.setText("Error: Word data missing");
                holder.textViewDetails.setText("");
                return; // Avoid crash
            }

            holder.textViewWord.setText(word.getWord() != null ? word.getWord().toUpperCase() : "NO WORD");

            StringBuilder details = new StringBuilder();
            if (word.getMeanings() != null && !word.getMeanings().isEmpty()) {
                for (Word.Meaning meaning : word.getMeanings()) {
                    if (meaning == null) {
                        Log.w(ADAPTER_TAG, "Null meaning found for word: " + word.getWord());
                        continue;
                    }
                    details.append("Part of Speech: ").append(meaning.getPartOfSpeech() != null ? meaning.getPartOfSpeech() : "N/A").append("\n");
                    details.append("Definition: ").append(meaning.getDefinition() != null ? meaning.getDefinition() : "N/A").append("\n");
                    if (meaning.getSynonyms() != null && !meaning.getSynonyms().isEmpty()) {
                        details.append("Synonyms: ").append(String.join(", ", meaning.getSynonyms())).append("\n");
                    }
                    details.append("\n");
                }
            } else {
                details.append("No detailed information available.");
            }
            holder.textViewDetails.setText(details.toString().trim());

            // Set initial arrow state
            holder.imageViewArrow.setRotation(holder.textViewDetails.getVisibility() == View.VISIBLE ? 90 : 0);

            holder.headerLayout.setOnClickListener(v -> {
                boolean isExpanding = holder.textViewDetails.getVisibility() == View.GONE;
                
                // Animate the arrow rotation
                holder.imageViewArrow.animate()
                    .rotation(isExpanding ? 90 : 0)
                    .setDuration(200)
                    .start();

                // Show/hide the details with animation
                if (isExpanding) {
                    holder.textViewDetails.setVisibility(View.VISIBLE);
                    holder.textViewDetails.setAlpha(0f);
                    holder.textViewDetails.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .start();
                } else {
                    holder.textViewDetails.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> holder.textViewDetails.setVisibility(View.GONE))
                        .start();
                }
            });
        }

        @SuppressLint("LongLogTag")
        @Override
        public int getItemCount() {
            int count = (wordList != null) ? wordList.size() : 0;
            Log.d(ADAPTER_TAG, "getItemCount called, returning: " + count);
            return count;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewWord;
            TextView textViewDetails;
            ImageView imageViewArrow;
            LinearLayout headerLayout;

            @SuppressLint("LongLogTag")
            ViewHolder(View itemView) {
                super(itemView);
                Log.d(ADAPTER_TAG, "ViewHolder created.");
                textViewWord = itemView.findViewById(R.id.textViewItemWord);
                textViewDetails = itemView.findViewById(R.id.textViewItemDetails);
                imageViewArrow = itemView.findViewById(R.id.imageViewArrow);
                headerLayout = itemView.findViewById(R.id.headerLayout);
                if (textViewWord == null) Log.e(ADAPTER_TAG, "ViewHolder: textViewItemWord is NULL!");
                if (textViewDetails == null) Log.e(ADAPTER_TAG, "ViewHolder: textViewItemDetails is NULL!");
                if (imageViewArrow == null) Log.e(ADAPTER_TAG, "ViewHolder: imageViewArrow is NULL!");
                if (headerLayout == null) Log.e(ADAPTER_TAG, "ViewHolder: headerLayout is NULL!");
            }
        }
    }
}