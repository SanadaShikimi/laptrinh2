// ResultActivity.java - Hiển thị kết quả quiz
package com.example.laptrinh2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private TextView tvScore, tvPercentage, tvResult, tvMessage;
    private ProgressBar progressScore;
    private Button btnPlayAgain, btnHome;

    private int score;
    private int totalQuestions;
    private double percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initViews();
        getDataFromIntent();
        displayResults();
        setupButtons();
    }

    private void initViews() {
        tvScore = findViewById(R.id.tvScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvResult = findViewById(R.id.tvResult);
        tvMessage = findViewById(R.id.tvMessage);
        progressScore = findViewById(R.id.progressScore);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnHome = findViewById(R.id.btnHome);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        totalQuestions = intent.getIntExtra("total", 5);
        percentage = (double) score / totalQuestions * 100;
    }

    private void displayResults() {
        tvScore.setText(score + "/" + totalQuestions);
        tvPercentage.setText(String.format("%.1f%%", percentage));

        progressScore.setMax(totalQuestions);
        progressScore.setProgress(score);

        // Hiển thị kết quả và thông điệp dựa trên điểm số
        if (percentage >= 80) {
            tvResult.setText("Xuất sắc!");
            tvMessage.setText("Bạn đã có kiến thức rất tốt về CNTT!");
            tvResult.setTextColor(getResources().getColor(R.color.green));
        } else if (percentage >= 60) {
            tvResult.setText("Khá tốt!");
            tvMessage.setText("Bạn cần ôn tập thêm một chút nữa.");
            tvResult.setTextColor(getResources().getColor(R.color.blue));
        } else if (percentage >= 40) {
            tvResult.setText("Trung bình");
            tvMessage.setText("Bạn nên học thêm để cải thiện kết quả.");
            tvResult.setTextColor(getResources().getColor(R.color.orange));
        } else {
            tvResult.setText("Cần cố gắng hơn");
            tvMessage.setText("Hãy dành thời gian học tập và luyện tập thêm.");
            tvResult.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void setupButtons() {
        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Ngăn không cho người dùng quay lại quiz
        super.onBackPressed();
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}