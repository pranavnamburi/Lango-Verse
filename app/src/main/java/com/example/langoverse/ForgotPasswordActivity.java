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
        buttonSendEmail =findViewById(R.id.buttonSendEmail);
        loginTextView=findViewById(R.id.textViewLogin);
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
    connect();

    }
    public void connect(){
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.execute(() -> {
        try {
            con = connectionClass.CONN();
            if (con == null) {
                str = "Error in connection with SQL server";
            } else {
                str = "Connected to MySQL server";
            }
        } catch (Exception e) {
            str = "Exception: " + e.getMessage();
        }
        runOnUiThread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    });
}
//    private  void forgotPassword(){
//        //please repair
//        //password retrieval
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                Connection con = connectionClass.CONN();
//                if (con == null) {
//                    throw new SQLException("Failed to establish connection");
//                }
//                String query = "SELECT password FROM userdb WHERE email = ?";
//                statement = con.prepareStatement(query);
//                statement.setString(1, emailEditText.getText().toString().trim());
//                ResultSet rs = statement.executeQuery();
//                if (rs.next()) {
//                    newpass = rs.getString("password");
//                } else {
//                    runOnUiThread(() -> Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show());
//
//
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//
//
//            }
//        });
//
//
//        //email service
//        String host = "smtp.outlook.com";
//        int port = 587;
//
//        // Create properties object and configure SMTP server
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", host);
//        props.put("mail.smtp.port", port);
//        String senderEmail = "buddy.travel@outlook.com";
//        String senderPassword = "buddy_travel_srmap";
//        // Create session with authenticator
//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(senderEmail, senderPassword);
//            }
//        });
//        try {
//            // Create message
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(senderEmail));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailEditText.getText().toString().trim()));
//            message.setSubject("Forget Password");
//            message.setText("Your updated password is : "+newpass+"\n\nUse this PASSWORD to login and under profile section you change your password using UPDATE PASSWORD\n\nRegards,\nTravel Buddy\n");
//            // Send message
//            Transport.send(message);
//            Toast.makeText(this, "Email Sent Successfully!", Toast.LENGTH_SHORT).show();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Email Not Sent, Please Try Again Later!", Toast.LENGTH_SHORT).show();
//        }
//        navigateToLogin();
//
//    };


    private void forgotPassword() {
        String userEmail=emailEditText.getText().toString().trim();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String retrievedPassword = null;
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    throw new SQLException("Failed to establish connection");
                }
                String query = "SELECT password FROM userdb WHERE email = ?";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, userEmail);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                retrievedPassword = rs.getString("password");
                } else {
                runOnUiThread(() -> Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show());
                return;
                }


            } catch (Exception e) {
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
            String host = "smtp.outlook.com";
            int port = 587;
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            String senderEmail = "buddy.travel@outlook.com";
            String senderPassword = "buddy_travel_srmap";
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailEditText.getText().toString().trim()));
                message.setSubject("Forget Password");
                message.setText("Your  password is: " + password + "\n\nRegards,\nLangoVerse\n");
                Transport.send(message);
                Toast.makeText(this, "Email Sent Successfully!", Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Email Not Sent, Please Try Again Later!", Toast.LENGTH_SHORT).show();
            }
            navigateToLogin();
        });
    }


    private void navigateToLogin() {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
    }
}
