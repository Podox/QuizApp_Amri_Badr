package com.example.amri_badr_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.amri_badr_app.models.Question;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
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

    private MediaRecorder audioRecorder;
    private File audioFile;

    private static final int PERMISSION_REQUEST_CODE = 123;

    private SurfaceView cameraPreview;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tvQuestion);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        rgOptions = findViewById(R.id.rgOptions);
        bNext = findViewById(R.id.bNext);
        cameraPreview = findViewById(R.id.cameraPreview);

        surfaceHolder = cameraPreview.getHolder();

        questions = new ArrayList<>();

        if (!checkPermissions()) {
            requestPermissions();
        }

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });

        fetchQuestions();

        bNext.setOnClickListener(v -> {
            if (rgOptions.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Merci de choisir une réponse S.V.P !", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedAnswer = findViewById(rgOptions.getCheckedRadioButtonId());
            String answerText = selectedAnswer.getText().toString();

            if (answerText.equals(questions.get(currentQuestionIndex).getCorrectAnswer())) {
                score++;
            }

            stopAudioRecording();

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
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

        tvQuestion.setText(question.getQuestionText());

        if (question.getImage() != null && !question.getImage().isEmpty()) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(question.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivQuestionImage);
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }

        rgOptions.removeAllViews();
        addRadioButton(question.getOption1());
        addRadioButton(question.getOption2());
        addRadioButton(question.getOption3());
        addRadioButton(question.getOption4());
        rgOptions.clearCheck();

        startAudioRecording();
    }

    private void addRadioButton(String option) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(option);
        radioButton.setTextSize(16);
        rgOptions.addView(radioButton);
    }

    private boolean checkPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED &&
                audio == PackageManager.PERMISSION_GRANTED &&
                storage == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    private void startAudioRecording() {
        try {
            audioFile = new File(getExternalFilesDir(null), "audio_" + currentQuestionIndex + ".3gp");
            audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            audioRecorder.setOutputFile(audioFile.getAbsolutePath());
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            audioRecorder.prepare();
            audioRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudioRecording() {
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
        }
    }

    private void openCamera() {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
