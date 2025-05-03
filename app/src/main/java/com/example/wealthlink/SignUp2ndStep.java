package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp2ndStep extends AppCompatActivity {
    EditText EmailInput, PasswordInput, VerifyPasswordInput;
    Button Login, SignUp;

    String firstName, middleName, lastName;

    // firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up2nd_step);

        EmailInput = (EditText) findViewById(R.id.inputEmail);
        PasswordInput = (EditText) findViewById(R.id.inputPasword);
        VerifyPasswordInput = (EditText) findViewById(R.id.inputVerifyPassword);

        Login = (Button) findViewById(R.id.btnLogin);
        SignUp = (Button) findViewById(R.id.btnSignUp);

        firstName = getIntent().getStringExtra("firstName");
        middleName = getIntent().getStringExtra("middleName");
        lastName = getIntent().getStringExtra("lastName");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(SignUp2ndStep.this, Login.class);
                startActivity(LoginIntent);
                finish();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailInput.getText().toString().trim();
                String password = PasswordInput.getText().toString().trim();
                String verifyPassword = VerifyPasswordInput.getText().toString().trim();

                if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (verifyPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please verify your password", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(verifyPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Passwords match, proceed to the next step
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Account created
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            String userId = firebaseUser.getUid(); // Get UID

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("firstName", firstName);
                                            user.put("middleName", middleName);
                                            user.put("lastName", lastName);

                                            // Store user info with UID as document ID
                                            db.collection("users").document(userId)
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(SignUp2ndStep.this, "User data saved", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignUp2ndStep.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(SignUp2ndStep.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // Intent to go to Homepage
                    Intent homeIntent = new Intent(SignUp2ndStep.this, wealthLinkMainPage.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        });

    }
}