package com.example.langoverse;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewHistoryActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    ResultSet rs;
    Connection con;
    private PreparedStatement statement;
    private TextView tvDatabaseResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        connectionClass = new ConnectionClass();
        tvDatabaseResults = findViewById(R.id.tvDatabaseResults);
        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Assuming connectionClass.CONN() handles exceptions internally and returns null on failure
                con = connectionClass.CONN();
                if (con == null) {
                    throw new SQLException("Failed to establish connection");
                }

                // Corrected SQL query to use a placeholder for the email
                String query = "SELECT sourceLanguagetext, sourceLanguageTitle, translatedText, destinationLangaugeTitle, translationTime FROM translatedb WHERE userEmail = ? ORDER BY translationTime DESC";
                statement = con.prepareStatement(query);

                // Fetch email from a hypothetical singleton class instance method
                String email = User.getInstance().getEmail(); // Ensure this method and class exist and are correctly implemented
                statement.setString(1, email); // Set email to the first placeholder

                // Execute the query without passing the SQL string to executeQuery
                rs = statement.executeQuery();

                StringBuilder stringBuilder = new StringBuilder();
                while (rs.next()) {
                    stringBuilder.append(rs.getString("sourceLanguagetext"))
                            .append(" | ").append(rs.getString("sourceLanguageTitle"))
                            .append(" | ").append(rs.getString("translatedText"))
                            .append(" | ").append(rs.getString("destinationLangaugeTitle"))
                            .append(" | ").append(rs.getString("translationTime"))
                            .append("\n");
                }
                String finalData = stringBuilder.toString();
                runOnUiThread(() -> tvDatabaseResults.setText(finalData));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Properly close resources
                try {
                    if (rs != null) rs.close();
                    if (statement != null) statement.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    }

