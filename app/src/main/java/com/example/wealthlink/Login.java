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

public class Login extends AppCompatActivity {
    Button Login, SignUp;
    EditText Email, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Login = (Button) findViewById(R.id.btnLogin);
        SignUp = (Button) findViewById(R.id.btnSignUp);

        Email = (EditText) findViewById(R.id.inputEmail);
        Password = (EditText) findViewById(R.id.inputPasword);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Temporary Verification, add proper when database is made
                // Update to check if Username or Password is empty or wrong after DB
                if (Email.getText().toString().trim().equals("francis@rale.co") && Password.getText().toString().trim().equals("123")) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    // Intent to go to main page after verification
                    Intent homeIntent = new Intent(Login.this, TransactionHistory.class);
                    startActivity(homeIntent);
                } else {
                    if (Email.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    } else if (Password.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(Login.this, SignUp.class);
                startActivity(signUpIntent);
            }
        });

    }
}