package com.example.wealthlink;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GroupSettingsProfile extends AppCompatActivity {

    private static final String TAG = "GroupSettingsProfile";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageButton btnBack;
    private CardView profileImageContainer;
    private ImageView profileImage;
    private EditText groupNameEditText;
    private EditText descriptionEditText;
    private TextView changeText;
    private TextView investmentGoalText;
    private Button btnSaveChanges;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private String groupID;
    private String groupName;
    private String groupProfilePicUrl;
    private String groupDescription;
    private String groupInvestmentGoalType;
    private Uri imageUri;
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Get data from intent
        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupName = intent.getStringExtra("groupName");
        groupProfilePicUrl = intent.getStringExtra("groupProfilePicUrl");

        if (groupID == null) {
            // Handle error - no groupID provided
            Toast.makeText(this, "Error: Group ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        profileImageContainer = findViewById(R.id.profileImageContainer);
        profileImage = findViewById(R.id.profileImage);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        changeText = findViewById(R.id.changeText);
        investmentGoalText = findViewById(R.id.investmentGoalText);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Set up initial data from intent
        if (groupName != null && !groupName.isEmpty()) {
            groupNameEditText.setText(groupName);
            Log.d(TAG, "Received group name from intent: " + groupName);
        }

        if (groupProfilePicUrl != null && !groupProfilePicUrl.isEmpty()) {
            Log.d(TAG, "Received profile pic URL from intent: " + groupProfilePicUrl);
            Glide.with(this)
                    .load(groupProfilePicUrl)
                    .placeholder(R.drawable.profimg)
                    .error(R.drawable.profimg)
                    .into(profileImage);
        }

        // Set up click listeners
        btnBack.setOnClickListener(v -> {
            finish(); // Go back to previous screen
        });

        profileImageContainer.setOnClickListener(v -> {
            // Open image selection dialog
            showImageSelectionOptions();
        });

        changeText.setOnClickListener(v -> {
            // Set focus to description field
            descriptionEditText.requestFocus();
        });

        investmentGoalText.setOnClickListener(v -> {
            // Show investment goal type selection dialog
            showInvestmentGoalTypeDialog();
        });

        btnSaveChanges.setOnClickListener(v -> {
            // Save the changes
            saveChanges();
        });

        // Load complete group data from Firebase
        loadGroupData();
    }

    private void showImageSelectionOptions() {
        CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageChanged = true;

            // Display the selected image
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.profimg)
                    .error(R.drawable.profimg)
                    .into(profileImage);
        }
    }

    private void showInvestmentGoalTypeDialog() {
        final String[] investmentTypes = {"Savings", "Investment Portfolio", "Hybrid"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Investment Goal Type");
        builder.setSingleChoiceItems(investmentTypes, -1, (dialog, which) -> {
            groupInvestmentGoalType = investmentTypes[which];
            investmentGoalText.setText(groupInvestmentGoalType);
            dialog.dismiss();
        });
        builder.show();
    }

    private void saveChanges() {
        // Validate inputs
        String newGroupName = groupNameEditText.getText().toString().trim();
        String newDescription = descriptionEditText.getText().toString().trim();

        if (newGroupName.isEmpty()) {
            groupNameEditText.setError("Group name cannot be empty");
            return;
        }

        // Show loading state
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Saving...");

        if (imageChanged && imageUri != null) {
            // First upload the new image if it was changed
            uploadImageAndSaveData(newGroupName, newDescription);
        } else {
            // Just save the text data
            saveGroupData(newGroupName, newDescription, groupProfilePicUrl);
        }
    }

    private void uploadImageAndSaveData(final String newGroupName, final String newDescription) {
        // Create a unique filename
        final String fileName = "group_profile_" + groupID + "_" + UUID.randomUUID().toString();
        StorageReference imgRef = storageRef.child("group_profile_images/" + fileName);

        imgRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Save the group data with the new image URL
                                saveGroupData(newGroupName, newDescription, downloadUri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnSaveChanges.setEnabled(true);
                        btnSaveChanges.setText("Save Changes");
                        Toast.makeText(GroupSettingsProfile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveGroupData(String newGroupName, String newDescription, String profilePicUrl) {
        Map<String, Object> groupData = new HashMap<>();
        // Update both possible field names to ensure compatibility
        groupData.put("groupName", newGroupName);
        groupData.put("name", newGroupName);
        groupData.put("description", newDescription);
        groupData.put("profilePicUrl", profilePicUrl);

        if (groupInvestmentGoalType != null && !groupInvestmentGoalType.isEmpty()) {
            groupData.put("investmentGoalType", groupInvestmentGoalType);
        }

        db.collection("groups").document(groupID)
                .update(groupData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        btnSaveChanges.setEnabled(true);
                        btnSaveChanges.setText("Save Changes");
                        Toast.makeText(GroupSettingsProfile.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnSaveChanges.setEnabled(true);
                        btnSaveChanges.setText("Save Changes");
                        Toast.makeText(GroupSettingsProfile.this, "Failed to save changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadGroupData() {
        Log.d(TAG, "Loading group data for group ID: " + groupID);

        // Get additional data from Firebase
        db.collection("groups").document(groupID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Group document exists");

                                // Check for group name in both fields
                                if (groupName == null || groupName.isEmpty()) {
                                    String nameFromDB = document.getString("groupName");
                                    if (nameFromDB == null || nameFromDB.isEmpty()) {
                                        nameFromDB = document.getString("name");
                                    }

                                    if (nameFromDB != null && !nameFromDB.isEmpty()) {
                                        groupName = nameFromDB;
                                        groupNameEditText.setText(groupName);
                                        Log.d(TAG, "Set group name from database: " + groupName);
                                    }
                                }

                                // Check for profile pic URL
                                if (groupProfilePicUrl == null || groupProfilePicUrl.isEmpty()) {
                                    groupProfilePicUrl = document.getString("profilePicUrl");
                                    if (groupProfilePicUrl != null && !groupProfilePicUrl.isEmpty()) {
                                        Log.d(TAG, "Loading profile pic from database URL: " + groupProfilePicUrl);
                                        Glide.with(GroupSettingsProfile.this)
                                                .load(groupProfilePicUrl)
                                                .placeholder(R.drawable.profimg)
                                                .error(R.drawable.profimg)
                                                .into(profileImage);
                                    }
                                }

                                // Get other data
                                groupDescription = document.getString("description");
                                if (groupDescription != null) {
                                    descriptionEditText.setText(groupDescription);
                                }

                                groupInvestmentGoalType = document.getString("investmentGoalType");
                                if (groupInvestmentGoalType != null && !groupInvestmentGoalType.isEmpty()) {
                                    investmentGoalText.setText(groupInvestmentGoalType);
                                } else {
                                    investmentGoalText.setText("Select investment goal type");
                                }
                            } else {
                                Log.w(TAG, "Group document does not exist");
                                Toast.makeText(GroupSettingsProfile.this, "Group not found in database", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Error loading group data", task.getException());
                            Toast.makeText(GroupSettingsProfile.this, "Error loading group data: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}