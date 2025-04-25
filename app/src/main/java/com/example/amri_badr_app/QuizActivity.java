package com.example.amri_badr_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.amri_badr_app.models.Question;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion;
    private ImageView ivQuestionImage;
    private RadioGroup rgOptions;
    private Button bNext;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize UI elements
        tvQuestion = findViewById(R.id.tvQuestion);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        rgOptions = findViewById(R.id.rgOptions);
        bNext = findViewById(R.id.bNext);

        questions = new ArrayList<>();

        // Fetch questions from Firestore
        fetchQuestions();

        // Handle Next button click
        bNext.setOnClickListener(v -> {
            if (rgOptions.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Merci de choisir une réponse S.V.P !", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected answer
            RadioButton selectedAnswer = findViewById(rgOptions.getCheckedRadioButtonId());
            String answerText = selectedAnswer.getText().toString();

            // Check if answer is correct
            if (answerText.equals(questions.get(currentQuestionIndex).getCorrectAnswer())) {
                score++;
            }

            // Move to next question or show result
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                // End of quiz, go to Score activity
                Intent intent = new Intent(QuizActivity.this, Score.class);
                intent.putExtra("score", score);
                intent.putExtra("total", questions.size());
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchQuestions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    questions.clear();
                    questions.addAll(queryDocumentSnapshots.toObjects(Question.class));
                    if (!questions.isEmpty()) {
                        currentQuestionIndex = 0;
                        displayQuestion();
                    } else {
                        Toast.makeText(this, "No questions found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayQuestion() {
        Question question = questions.get(currentQuestionIndex);

        // Update question text
        tvQuestion.setText(question.getQuestionText());

        // Update image
        if (question.getImage() != null && !question.getImage().isEmpty()) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(question.getImage()) // URL from Firestore, e.g., "https://abdessamadnassih.com/mobile/drawable/q1.jpg"
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(ivQuestionImage);
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }

        // Clear previous options
        rgOptions.removeAllViews();

        // Add new radio buttons
        addRadioButton(question.getOption1());
        addRadioButton(question.getOption2());
        addRadioButton(question.getOption3());
        addRadioButton(question.getOption4());

        // Clear selection
        rgOptions.clearCheck();
    }

    private void addRadioButton(String option) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(option);
        radioButton.setTextSize(16);
        rgOptions.addView(radioButton);
    }
}