package com.example.credclone;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
      private ImageView backButton;
    private ImageView settingsButton;
    private ImageView profileImage;
    private ImageView editProfileButton;
    private EditText userName;
    private EditText userEmail;
    private TextView creditScore;
    private LinearLayout membershipCard;
    private LinearLayout paymentMethods;
    private LinearLayout rewards;
    private LinearLayout referrals;
    private LinearLayout help;
    private LinearLayout about;

    // Activity result launcher for image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });        initializeViews();
        setupImagePickerLauncher();
        setupClickListeners();
        setupEditTextListeners();
        loadUserData();
    }    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        settingsButton = findViewById(R.id.settings_button);
        profileImage = findViewById(R.id.profile_image);
        editProfileButton = findViewById(R.id.edit_profile_button);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        creditScore = findViewById(R.id.credit_score);
        membershipCard = findViewById(R.id.membership_card);
        paymentMethods = findViewById(R.id.payment_methods);
        rewards = findViewById(R.id.rewards);
        referrals = findViewById(R.id.referrals);
        help = findViewById(R.id.help);        about = findViewById(R.id.about);
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Set the selected image to the ImageView
                        profileImage.setImageURI(selectedImageUri);
                        profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        showToast("Profile picture updated!");
                    }
                }
            }
        );
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        settingsButton.setOnClickListener(v -> 
            showToast("Settings clicked"));        profileImage.setOnClickListener(v -> 
            openImagePicker());

        editProfileButton.setOnClickListener(v -> 
            toggleEditMode());

        membershipCard.setOnClickListener(v ->
            showToast("Membership details"));

        paymentMethods.setOnClickListener(v -> 
            showToast("Payment methods"));

        rewards.setOnClickListener(v -> 
            showToast("Rewards & Cashback"));

        referrals.setOnClickListener(v -> 
            showToast("Refer friends"));

        help.setOnClickListener(v -> 
            showToast("Help & Support"));        about.setOnClickListener(v -> 
            showToast("About Cred"));
    }    private void setupEditTextListeners() {
        // Save name when user finishes editing
        userName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                saveUserName();
                disableEditMode();
                return true;
            }
            return false;
        });

        // Save name when focus is lost
        userName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveUserName();
                disableEditMode();
            }
        });

        // Save email when user finishes editing
        userEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveUserEmail();
                disableEditMode();
                return true;
            }
            return false;
        });

        // Save email when focus is lost
        userEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveUserEmail();
                disableEditMode();
            }
        });
    }private void loadUserData() {
        // Load data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ProfileData", MODE_PRIVATE);
        
        String savedName = prefs.getString("user_name", "John Doe");
        String savedEmail = prefs.getString("user_email", "john.doe@email.com");
        String savedCreditScore = prefs.getString("credit_score", "785");
          userName.setText(savedName);
        userEmail.setText(savedEmail);
        creditScore.setText(savedCreditScore);
    }

    private void saveUserName() {
        String name = userName.getText().toString().trim();
        if (!name.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("ProfileData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", name);
            editor.apply();
        }
    }

    private void saveUserEmail() {
        String email = userEmail.getText().toString().trim();
        if (!email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SharedPreferences prefs = getSharedPreferences("ProfileData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_email", email);
            editor.apply();
        } else if (!email.isEmpty()) {
            showToast("Please enter a valid email address");
            // Reload the previous valid email
            String savedEmail = getSharedPreferences("ProfileData", MODE_PRIVATE)
                    .getString("user_email", "john.doe@email.com");
            userEmail.setText(savedEmail);        }
    }

    private void toggleEditMode() {
        if (userName.isFocusable()) {
            // Currently in edit mode, disable it
            disableEditMode();
            saveUserName();
            saveUserEmail();
        } else {
            // Currently in view mode, enable edit mode
            enableEditMode();
        }
    }

    private void enableEditMode() {
        // Make fields editable
        userName.setFocusable(true);
        userName.setFocusableInTouchMode(true);
        userName.setCursorVisible(true);
        
        userEmail.setFocusable(true);
        userEmail.setFocusableInTouchMode(true);
        userEmail.setCursorVisible(true);
        
        // Focus on name field and show keyboard
        userName.requestFocus();
        userName.selectAll();
        
        showToast("Edit mode enabled");
    }

    private void disableEditMode() {
        // Make fields non-editable
        userName.setFocusable(false);
        userName.setFocusableInTouchMode(false);
        userName.setCursorVisible(false);
        userName.clearFocus();
        
        userEmail.setFocusable(false);
        userEmail.setFocusableInTouchMode(false);
        userEmail.setCursorVisible(false);
        userEmail.clearFocus();
        
        showToast("Changes saved");
    }

    private void openImagePicker() {
        // Check if we have permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, open image picker
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch image picker
                launchImagePicker();
            } else {
                // Permission denied
                showToast("Permission denied. Cannot access gallery.");
            }
        }
    }    @Override
    protected void onPause() {
        super.onPause();
        // Save any pending changes and disable edit mode when the user leaves the activity
        if (userName.isFocusable()) {
            saveUserName();
            saveUserEmail();
            disableEditMode();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
