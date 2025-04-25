package com.example.amri_badr_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Score extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        TextView tvScore = findViewById(R.id.tvScore);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button bTry = findViewById(R.id.bTry);
        Button bLogout = findViewById(R.id.bLogout);

        // Get score and total from intent
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 1);

        // Calculate percentage
        int percentage = (int) ((score * 100.0) / total);

        // Update UI
        tvScore.setText(String.format("%d%%", percentage));
        progressBar.setProgress(percentage);

        // Try again button: Restart quiz
        bTry.setOnClickListener(v -> {
            Intent intent = new Intent(Score.this, QuizActivity.class);
            startActivity(intent);
            finish();
        });

        // Logout button: Sign out and go to LoginActivity
        bLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Score.this, MainActivity.class); // Replace with your login activity
            startActivity(intent);
            finish();
        });
    }
}