package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button signUpButton;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);

        // Set up sign up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input fields (simplified validation)
                if (validateFields()) {
                    String name = Objects.requireNonNull(nameEditText.getText()).toString();
                    String username = Objects.requireNonNull(usernameEditText.getText()).toString();
                    String email = Objects.requireNonNull(emailEditText.getText()).toString();
                    String password = Objects.requireNonNull(passwordEditText.getText()).toString();

                    Log.d(TAG, "Name: " + name);
                    Log.d(TAG, "Username: " + username);
                    Log.d(TAG, "Email: " + email);
                    Log.d(TAG, "Password: " + password);

                    // Navigate to the home page after successful sign up
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (Objects.requireNonNull(nameEditText.getText()).toString().isEmpty()) {
            nameEditText.setError("Please enter your name");
            isValid = false;
        }

        if (Objects.requireNonNull(usernameEditText.getText()).toString().isEmpty()) {
            usernameEditText.setError("Please enter your username");
            isValid = false;
        }

        if (Objects.requireNonNull(emailEditText.getText()).toString().isEmpty()) {
            emailEditText.setError("Please enter your email");
            isValid = false;
        }

        if (Objects.requireNonNull(passwordEditText.getText()).toString().isEmpty()) {
            passwordEditText.setError("Please enter your password");
            isValid = false;
        }

        return isValid;
    }
}