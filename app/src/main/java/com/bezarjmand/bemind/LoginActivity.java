package com.bezarjmand.bemind;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button loginButton;
    private Button createAccountButton;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Open the database
        db = openOrCreateDatabase("MotivationalDB", MODE_PRIVATE, null);

        // Create the "users" table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT)");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                if (!username.isEmpty()) {
                    if (isExistingUser(username)) {
                        // Display a welcome back message
                        Toast.makeText(LoginActivity.this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();

                        // Start the MotivationalActivity
                        Intent intent = new Intent(LoginActivity.this, MotivationalActivity.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the LoginActivity to prevent going back on back press

                        // Hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } else {
                        Toast.makeText(LoginActivity.this, "User does not exist. Please create an account.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });



        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                if (!username.isEmpty()) {
                    if (!isExistingUser(username)) {
                        // Save the username to the database
                        saveUsernameToDatabase(username);

                        // Display an account created message
                        Toast.makeText(LoginActivity.this, "Account created for " + username, Toast.LENGTH_SHORT).show();

                        // Start the MotivationalActivity
                        Intent intent = new Intent(LoginActivity.this, MotivationalActivity.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the LoginActivity to prevent going back on back press
                    } else {
                        Toast.makeText(LoginActivity.this, "User already exists. Please log in.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isExistingUser(String username) {
        // Query the "users" table to check if the username exists
        String query = "SELECT * FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void saveUsernameToDatabase(String username) {
        // Insert the new username into the table
        ContentValues values = new ContentValues();
        values.put("username", username);
        db.insert("users", null, values);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database
        db.close();
    }
}
