package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.login);
            Log.d(TAG, "LoginActivity onCreate started");

            if (!initializeViews()) {
                Log.e(TAG, "Failed to initialize views - some UI elements are missing");
                Toast.makeText(this, "Error loading login screen. Some UI elements are missing.", Toast.LENGTH_LONG).show();
                return;
            }

            // Set up login button click listener
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String username = Objects.requireNonNull(usernameEditText.getText()).toString();
                        String password = Objects.requireNonNull(passwordEditText.getText()).toString();

                        Log.d(TAG, "Username: " + username);
                        Log.d(TAG, "Password: " + password);
                        
                        // For now, accept any non-empty credentials
                        if (username.trim().isEmpty() || password.trim().isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Navigate to the home page after successful login
                        Log.d(TAG, "Login successful, starting HomeActivity");
                        try {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish(); // Close the login activity to prevent going back
                        } catch (Exception e) {
                            Log.e(TAG, "Error starting HomeActivity: " + e.getMessage(), e);
                            Toast.makeText(LoginActivity.this, "Error starting home screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in login process: " + e.getMessage(), e);
                        Toast.makeText(LoginActivity.this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            
            Log.d(TAG, "LoginActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in LoginActivity onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing login screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private boolean initializeViews() {
        try {
            usernameEditText = findViewById(R.id.usernameEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            
            boolean allViewsFound = true;
            
            if (usernameEditText == null) {
                Log.e(TAG, "usernameEditText is null");
                allViewsFound = false;
            }
            if (passwordEditText == null) {
                Log.e(TAG, "passwordEditText is null");
                allViewsFound = false;
            }
            if (loginButton == null) {
                Log.e(TAG, "loginButton is null");
                allViewsFound = false;
            }
            
            return allViewsFound;
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            return false;
        }
    }
}