package com.example.langoverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private ConnectionClass connectionClass;  // Ensure this is properly initialized
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        connectionClass = new ConnectionClass();  // Assuming this properly sets up the connection

        usernameEditText = findViewById(R.id.editTextUsername);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.buttonRegister);
        loginTextView = findViewById(R.id.textViewLogin);

        registerButton.setOnClickListener(v -> registerUser());
        loginTextView.setOnClickListener(v -> navigateToLogin());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();  // Consider hashing this password

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try (Connection conn = connectionClass.CONN()) {  // Use try-with-resources to ensure closure
                if (conn == null) {
                    throw new SQLException("Failed to establish connection");
                }

                String sql = "INSERT INTO userdb (username, email, password) VALUES (?, ?, ?)";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, username);
                    statement.setString(2, email);
                    statement.setString(3, password);  // Ensure this is a hashed password

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        executorService.shutdown();
    }

    private void navigateToLogin() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}
