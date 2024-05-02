package com.example.langoverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import androidx.annotation.Nullable;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    Connection con;
    String str;

    private EditText emailEditText;
    private Button buttonSendEmail;
    private TextView loginTextView;
    private PreparedStatement statement;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        //content missing
        //pls repair
        emailEditText = findViewById(R.id.editTextEmail);
        buttonSendEmail = findViewById(R.id.buttonSendEmail);
        loginTextView = findViewById(R.id.textViewLogin);
        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
        connectionClass = new ConnectionClass();

    }



    private void forgotPassword() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String retrievedPassword = null;
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    throw new SQLException("Failed to establish connection");
                }
                String query = "SELECT password FROM userdb WHERE email = ?";
                try (PreparedStatement statement = con.prepareStatement(query)) {
                    statement.setString(1, emailEditText.getText().toString().trim());
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            retrievedPassword = rs.getString("password");
                        } else {
                            runOnUiThread(() -> Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show());
                            return;
                        }
                    }
                }
            } catch (SQLException e) {
                runOnUiThread(() -> Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }

            if (retrievedPassword != null) {
                sendEmail(retrievedPassword);
            }
        });
    }



    private void sendEmail(String password) {
        runOnUiThread(() -> {
            Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
        });

        navigateToLogin();
    }






    private void navigateToLogin() {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
    }
}
