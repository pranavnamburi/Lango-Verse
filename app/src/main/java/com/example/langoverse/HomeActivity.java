package com.example.langoverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    User user;
    private Button BtnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BtnLogout = findViewById(R.id.BtnLogout);

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                user.setEmail(null);
                User.getInstance().setEmail(null);
                Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(HomeActivity.this,"Logged Out SuccessFully", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void startVoiceTranslationActivity(View view) {
        Intent intent = new Intent(this, VoiceTranslationActivity.class);
        startActivity(intent);
    }

    public void startTextTranslationActivity(View view) {
        Intent intent = new Intent(this, TextTranslationActivity.class);
        startActivity(intent);
    }
    public void startViewHistoryActivity(View view){
        Intent intent = new Intent(this, ViewHistoryActivity.class);
        startActivity(intent);
    }


}
