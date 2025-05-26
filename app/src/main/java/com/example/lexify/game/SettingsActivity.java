package com.example.lexify.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lexify.R;
import com.example.lexify.utils.ToastUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "LexifyGamePrefs";
    private static final String KEY_PLAYER_NAME = "playerName";
    private static final String KEY_PROFILE_PICTURE = "profilePicture";

    private ImageView profilePicture;
    private TextInputEditText playerNameEditText;
    private MaterialButton saveButton;
    private SharedPreferences prefs;
    private Uri currentProfilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize preferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        profilePicture = findViewById(R.id.profile_picture);
        playerNameEditText = findViewById(R.id.player_name_edit_text);
        saveButton = findViewById(R.id.save_button);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Load saved player name
        String savedPlayerName = prefs.getString(KEY_PLAYER_NAME, "Player");
        playerNameEditText.setText(savedPlayerName);

        // Load saved profile picture if exists
        String savedProfilePicture = prefs.getString(KEY_PROFILE_PICTURE, null);
        if (savedProfilePicture != null) {
            currentProfilePictureUri = Uri.parse(savedProfilePicture);
            profilePicture.setImageURI(currentProfilePictureUri);
        }

        // Set up save button click listener
        saveButton.setOnClickListener(v -> saveChanges());
    }

    public void changeProfilePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            currentProfilePictureUri = data.getData();
            profilePicture.setImageURI(currentProfilePictureUri);
        }
    }

    private void saveChanges() {
        String newPlayerName = playerNameEditText.getText().toString().trim();
        
        if (newPlayerName.isEmpty()) {
            playerNameEditText.setError("Please enter a name");
            return;
        }

        // Save changes to preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PLAYER_NAME, newPlayerName);
        if (currentProfilePictureUri != null) {
            editor.putString(KEY_PROFILE_PICTURE, currentProfilePictureUri.toString());
        }
        editor.apply();

        // Show success message using custom toast
        ToastUtils.showCustomToast(this, "Changes saved successfully!", true);
        
        // Return to previous screen
        finish();
    }
}

