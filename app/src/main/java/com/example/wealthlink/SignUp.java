package com.example.wealthlink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {

    EditText FirstNameInput, MiddleNameInput, LastNameInput;
    Button Login, NextStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        FirstNameInput = (EditText) findViewById(R.id.inputFirstName);
        MiddleNameInput = (EditText) findViewById(R.id.inputMiddleName);
        LastNameInput = (EditText) findViewById(R.id.inputLastName);

        Login = (Button) findViewById(R.id.btnLogin);
        NextStep = (Button) findViewById(R.id.btnNextStep);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(SignUp.this, Login.class);
                startActivity(LoginIntent);
                finish();
            }
        });


        NextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = FirstNameInput.getText().toString().trim();
                String middleName = MiddleNameInput.getText().toString().trim();
                String lastName = LastNameInput.getText().toString().trim();

                if (firstName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your first name", Toast.LENGTH_SHORT).show();
                } else if (middleName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your middle name", Toast.LENGTH_SHORT).show();
                } else if (lastName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your last name", Toast.LENGTH_SHORT).show();
                } else {
                    // All fields filled, continue to next step
                    Intent NextStepIntent = new Intent(SignUp.this, SignUp2ndStep.class);
                    NextStepIntent.putExtra("firstName", firstName);
                    NextStepIntent.putExtra("middleName", middleName);
                    NextStepIntent.putExtra("lastName", lastName);
                    startActivity(NextStepIntent);
                    finish();
                }
            }
        });
    }


}