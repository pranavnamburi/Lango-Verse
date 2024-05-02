package com.example.langoverse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen without title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Hide action bar if your app has one
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.progressBar);

        progressBar.setMax(100); // Set the maximum progress value
        progressBar.setProgress(50); // Set the initial progress value

        // Handler to delay the splash screen and navigate to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent to navigate to the next activity (in this case, LoginActivity)
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to the splash screen
            }
        }, SPLASH_TIMEOUT);
    }
}
