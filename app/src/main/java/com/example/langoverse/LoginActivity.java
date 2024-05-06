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
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Connection con;
    String str;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private TextView forgotTextView;
    private PreparedStatement statement;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectionClass = new ConnectionClass();


//         Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // User is already logged in, navigate to the home page
//            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//            finish(); // Finish this activity to prevent going back to the login screen
//        }
//        User user = User.getInstance();
//        if (user.getEmail() != null) {
//            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//            finish();
//        }

        // Find UI components
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerTextView = findViewById(R.id.textViewRegister);
        forgotTextView=findViewById(R.id.textForgotPassword);


        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Set click listener for register text view
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
        forgotTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToForgotPassword();
            }
        });

    }


    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success
//                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                            // Navigate to the home page
//                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                            finish(); // Finish this activity to prevent going back to the login screen
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        executorService.execute(() -> {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    throw new SQLException("Failed to establish connection");
                }

                String sql = "SELECT * FROM userdb WHERE email = ? AND password = ?";
                PreparedStatement statement = con.prepareStatement(sql);
                statement.setString(1, email);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    User.getInstance().setEmail(email);
//                    runOnUiThread(()->{user.setEmail(email);});
                    runOnUiThread(() -> {

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                    });
                } else {
                    runOnUiThread(() -> {

                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {

                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });executorService.shutdown();

    }

    private void navigateToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
    private void navigateToForgotPassword(){
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}
