package com.example.langoverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
         try{
        String stringSenderEmail = "YOUR-EMAIL";
        String stringReceiverEmail = emailEditText.getText().toString().trim();
        String stringSubject = "Password Recovery - Langoverse";
        String stringMessage = "Your password for Accessing LangoVerse is: \n" + password;
        String stringPasswordSenderEmail = "dcvbwcjcbgoynsmf -apppassword";
        String stringHost = "smtp.gmail.com";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", stringHost);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));
        mimeMessage.setSubject(stringSubject);
        mimeMessage.setText(stringMessage);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Transport.send(mimeMessage);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
         } catch (AddressException e) {
             e.printStackTrace();
         } catch (MessagingException e) {
             e.printStackTrace();
         }

        runOnUiThread(() -> Toast.makeText(this, "Email Sent Successfully!!!, Please check your Email.", Toast.LENGTH_SHORT).show());

        navigateToLogin();
    }






    private void navigateToLogin() {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
    }
}
