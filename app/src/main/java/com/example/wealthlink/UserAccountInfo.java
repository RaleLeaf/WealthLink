package com.example.wealthlink;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserAccountInfo extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private DocumentReference userDocRef;

    // Maps to store EditText fields and their corresponding Change buttons
    private Map<String, EditText> editTextFields = new HashMap<>();
    private Map<String, TextView> changeButtons = new HashMap<>();
    private Map<String, Boolean> isEditMode = new HashMap<>();

    // Calendar for date selection
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_info);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            userDocRef = db.collection("users").document(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.US);

        // Back button functionality
        ImageView backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initializeFields();
        setupChangeButtons();
        loadUserData();
    }

    private void initializeFields() {
        // EditTexts
        editTextFields.put("firstName", findViewById(R.id.edit_firstName));
        editTextFields.put("middleName", findViewById(R.id.edit_middleName));
        editTextFields.put("lastName", findViewById(R.id.edit_lastName));
        editTextFields.put("birthdate", findViewById(R.id.edit_birthdate));
        editTextFields.put("gender", findViewById(R.id.edit_gender));
        editTextFields.put("phone", findViewById(R.id.edit_phone));
        editTextFields.put("address", findViewById(R.id.edit_address));
        editTextFields.put("province", findViewById(R.id.edit_province));
        editTextFields.put("region", findViewById(R.id.edit_region));

        // Buttons
        changeButtons.put("firstName", findViewById(R.id.change_firstName));
        changeButtons.put("middleName", findViewById(R.id.change_middleName));
        changeButtons.put("lastName", findViewById(R.id.change_lastName));
        changeButtons.put("birthdate", findViewById(R.id.change_birthdate));
        changeButtons.put("gender", findViewById(R.id.change_gender));
        changeButtons.put("phone", findViewById(R.id.change_phone));
        changeButtons.put("address", findViewById(R.id.change_address));
        changeButtons.put("province", findViewById(R.id.change_province));
        changeButtons.put("region", findViewById(R.id.change_region));

        for (String key : editTextFields.keySet()) {
            isEditMode.put(key, false);
        }
    }

    private void setupChangeButtons() {
        for (final String key : changeButtons.keySet()) {
            final TextView changeButton = changeButtons.get(key);
            final EditText editText = editTextFields.get(key);

            if (editText != null && changeButton != null) {
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (isEditMode.get(key) && s.toString().isEmpty()) {
                            editText.requestFocus();
                        }
                    }
                });

                changeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean currentEditMode = isEditMode.get(key);

                        if (currentEditMode) {
                            editText.setEnabled(false);
                            changeButton.setText("Change");

                            saveChanges(key, editText.getText().toString());

                        } else {
                            if (key.equals("birthdate")) {
                                showDatePickerDialog();
                            } else {
                                editText.setEnabled(true);
                                editText.requestFocus();
                            }
                            changeButton.setText("Save");
                        }

                        isEditMode.put(key, !currentEditMode);
                    }
                });
            }
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateField() {
        EditText birthdateEditText = editTextFields.get("birthdate");
        if (birthdateEditText != null) {
            birthdateEditText.setText(dateFormatter.format(calendar.getTime()));
        }
    }

    private void loadUserData() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String middleName = documentSnapshot.getString("middleName");
                String lastName = documentSnapshot.getString("lastName");
                String gender = documentSnapshot.getString("gender");
                String address = documentSnapshot.getString("address");
                String province = documentSnapshot.getString("province");
                String region = documentSnapshot.getString("region");
                String phone = documentSnapshot.getString("phoneNumber");
                String birthdate = documentSnapshot.getString("birthdate");

                EditText firstNameEditText = editTextFields.get("firstName");
                if (firstNameEditText != null) {
                    firstNameEditText.setText(firstName != null ? firstName : "-");
                }

                EditText middleNameEditText = editTextFields.get("middleName");
                if (middleNameEditText != null) {
                    middleNameEditText.setText(middleName != null ? middleName : "-");
                }

                EditText lastNameEditText = editTextFields.get("lastName");
                if (lastNameEditText != null) {
                    lastNameEditText.setText(lastName != null ? lastName : "-");
                }

                EditText genderEditText = editTextFields.get("gender");
                if (genderEditText != null) genderEditText.setText(gender != null ? gender : "-");

                EditText phoneEditText = editTextFields.get("phone");
                if (phoneEditText != null) phoneEditText.setText(phone != null ? phone : "-");

                EditText addressEditText = editTextFields.get("address");
                if (addressEditText != null) addressEditText.setText(address != null ? address : "-");

                EditText provinceEditText = editTextFields.get("province");
                if (provinceEditText != null) provinceEditText.setText(province != null ? province : "-");

                EditText regionEditText = editTextFields.get("region");
                if (regionEditText != null) regionEditText.setText(region != null ? region : "-");

                EditText birthdateEditText = editTextFields.get("birthdate");
                if (birthdateEditText != null) birthdateEditText.setText(birthdate != null ? birthdate : "-");

                EditText emailEditText = findViewById(R.id.edit_email);
                if (emailEditText != null) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        emailEditText.setText(user.getEmail());
                    }
                }

            } else {
                Toast.makeText(UserAccountInfo.this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UserAccountInfo.this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveChanges(String field, String newValue) {
        Map<String, Object> updates = new HashMap<>();

        String valueToStore = newValue.trim().equals("-") ? "" : newValue.trim();

        if (valueToStore.isEmpty()) {
            EditText editText = editTextFields.get(field);
            if (editText != null) {
                editText.setText("-");
            }
        }

        switch (field) {
            case "firstName":
                updates.put("firstName", valueToStore);
                break;
            case "middleName":
                updates.put("middleName", valueToStore);
                break;
            case "lastName":
                updates.put("lastName", valueToStore);
                break;
            case "birthdate":
                updates.put("birthdate", valueToStore);
                break;
            case "gender":
                updates.put("gender", valueToStore);
                break;
            case "phone":
                updates.put("phoneNumber", valueToStore);
                break;
            case "address":
                updates.put("address", valueToStore);
                break;
            case "province":
                updates.put("province", valueToStore);
                break;
            case "region":
                updates.put("region", valueToStore);
                break;
        }

        if (!updates.isEmpty()) {
            userDocRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserAccountInfo.this, field + " updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserAccountInfo.this, "Failed to update " + field + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}