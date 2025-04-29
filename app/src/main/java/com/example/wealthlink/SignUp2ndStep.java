package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp2ndStep extends AppCompatActivity {
    EditText EmailInput, PasswordInput, VerifyPasswordInput;
    Button Login, SignUp;

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

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(SignUp2ndStep.this, Login.class);
                startActivity(LoginIntent);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(getApplicationContext(), "Passwords match. Proceeding to next step...", Toast.LENGTH_SHORT).show();
                    // Intent to go to Homepage
                    Intent homeIntent = new Intent(SignUp2ndStep.this, TransactionHistory.class);
                    startActivity(homeIntent);
                }
            }
        });

    }
}