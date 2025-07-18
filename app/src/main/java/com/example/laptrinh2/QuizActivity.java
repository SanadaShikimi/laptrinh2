// QuizActivity.java - Chức năng 3: Trả lời trắc nghiệm
package com.example.laptrinh2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laptrinh2.database.DatabaseHelper;
import com.example.laptrinh2.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion, tvQuestionCount, tvTimer, tvScore;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext, btnPrevious;
    private ProgressBar progressBar;
    private LinearLayout llTrueFalse;
    private Button btnTrue, btnFalse;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions;
    private CountDownTimer questionTimer;
    private CountDownTimer totalTimer;
    private long timePerQuestion = 30000; // 30 giây mỗi câu
    private long totalTime = 300000; // 5 phút tổng

    private SharedPreferences settings;
    private boolean soundEnabled;
    private boolean timerEnabled;
    private boolean backgroundMusicEnabled;
    private MediaPlayer backgroundMusic;
    private MediaPlayer correctSound;
    private MediaPlayer incorrectSound;

    private SQLiteDatabase database;
    private String selectedAnswer = "";
    private List<String> userAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        loadSettings();
        initSounds();
        loadQuestions();
        setupQuiz();
        displayQuestion();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvTimer = findViewById(R.id.tvTimer);
        tvScore = findViewById(R.id.tvScore);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        progressBar = findViewById(R.id.progressBar);
        llTrueFalse = findViewById(R.id.llTrueFalse);
        btnTrue = findViewById(R.id.btnTrue);
        btnFalse = findViewById(R.id.btnFalse);

        btnNext.setOnClickListener(v -> nextQuestion());
        btnPrevious.setOnClickListener(v -> previousQuestion());

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                selectedAnswer = selected.getText().toString();
            }
        });

        btnTrue.setOnClickListener(v -> {
            selectedAnswer = "True";
            btnTrue.setBackgroundColor(getResources().getColor(R.color.selected_answer));
            btnFalse.setBackgroundColor(getResources().getColor(R.color.default_button));
        });

        btnFalse.setOnClickListener(v -> {
            selectedAnswer = "False";
            btnFalse.setBackgroundColor(getResources().getColor(R.color.selected_answer));
            btnTrue.setBackgroundColor(getResources().getColor(R.color.default_button));
        });
    }

    private void loadSettings() {
        settings = getSharedPreferences("quiz_settings", MODE_PRIVATE);
        soundEnabled = settings.getBoolean("sound_effects", false);
        timerEnabled = settings.getBoolean("timer_enabled", false);
        backgroundMusicEnabled = settings.getBoolean("background_music", false);
        totalQuestions = settings.getInt("questions_count", 5);

        if (!timerEnabled) {
            tvTimer.setVisibility(View.GONE);
        }
    }

    private void initSounds() {
        if (soundEnabled) {
            correctSound = MediaPlayer.create(this, R.raw.correct_sound);
            incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);
        }

        if (backgroundMusicEnabled) {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();
        userAnswers = new ArrayList<>();

        // Giả sử bạn có DatabaseHelper để truy cập database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM questions ORDER BY RANDOM() LIMIT ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(totalQuestions)});

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();

                // SỬA LỖI 3: Kiểm tra column index trước khi sử dụng
                int idIndex = cursor.getColumnIndex("id");
                int questionTextIndex = cursor.getColumnIndex("question_text");
                int optionAIndex = cursor.getColumnIndex("option_a");
                int optionBIndex = cursor.getColumnIndex("option_b");
                int optionCIndex = cursor.getColumnIndex("option_c");
                int optionDIndex = cursor.getColumnIndex("option_d");
                int correctAnswerIndex = cursor.getColumnIndex("correct_answer");
                int questionTypeIndex = cursor.getColumnIndex("question_type");
                int categoryIndex = cursor.getColumnIndex("category");

                if (idIndex != -1) {
                    question.setQuestionId(cursor.getInt(idIndex));
                }
                if (questionTextIndex != -1) {
                    question.setQuestionText(cursor.getString(questionTextIndex));
                }
                if (optionAIndex != -1) {
                    question.setOptionA(cursor.getString(optionAIndex));
                }
                if (optionBIndex != -1) {
                    question.setOptionB(cursor.getString(optionBIndex));
                }
                if (optionCIndex != -1) {
                    question.setOptionC(cursor.getString(optionCIndex));
                }
                if (optionDIndex != -1) {
                    question.setOptionD(cursor.getString(optionDIndex));
                }
                if (correctAnswerIndex != -1) {
                    question.setCorrectAnswer(cursor.getString(correctAnswerIndex));
                }

                // SỬA LỖI 2: Chuyển đổi int thành String cho setQuestionType
                if (questionTypeIndex != -1) {
                    int questionTypeValue = cursor.getInt(questionTypeIndex);
                    question.setQuestionType(String.valueOf(questionTypeValue));
                }

                if (categoryIndex != -1) {
                    question.setCategory(cursor.getString(categoryIndex));
                }

                questionList.add(question);
                userAnswers.add(""); // Khởi tạo câu trả lời rỗng
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setupQuiz() {
        progressBar.setMax(totalQuestions);
        tvScore.setText("Điểm: 0/" + totalQuestions);

        if (timerEnabled) {
            startTotalTimer();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            finishQuiz();
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);

        tvQuestion.setText(currentQuestion.getQuestionText());
        tvQuestionCount.setText("Câu " + (currentQuestionIndex + 1) + "/" + totalQuestions);
        progressBar.setProgress(currentQuestionIndex + 1);

        // Reset selection
        selectedAnswer = "";
        rgOptions.clearCheck();
        resetTrueFalseButtons();

        // Hiển thị câu trả lời đã chọn trước đó (nếu có)
        String previousAnswer = userAnswers.get(currentQuestionIndex);

        // SỬA LỖI 1: So sánh String với String thay vì String với int
        if ("1".equals(currentQuestion.getQuestionType())) { // True/False
            showTrueFalseOptions();
            if (!previousAnswer.isEmpty()) {
                if (previousAnswer.equals("True")) {
                    btnTrue.setBackgroundColor(getResources().getColor(R.color.selected_answer));
                    selectedAnswer = "True";
                } else if (previousAnswer.equals("False")) {
                    btnFalse.setBackgroundColor(getResources().getColor(R.color.selected_answer));
                    selectedAnswer = "False";
                }
            }
        } else { // Multiple Choice
            showMultipleChoiceOptions(currentQuestion);
            if (!previousAnswer.isEmpty()) {
                if (previousAnswer.equals(currentQuestion.getOptionA())) {
                    rbOption1.setChecked(true);
                } else if (previousAnswer.equals(currentQuestion.getOptionB())) {
                    rbOption2.setChecked(true);
                } else if (previousAnswer.equals(currentQuestion.getOptionC())) {
                    rbOption3.setChecked(true);
                } else if (previousAnswer.equals(currentQuestion.getOptionD())) {
                    rbOption4.setChecked(true);
                }
                selectedAnswer = previousAnswer;
            }
        }

        // Cập nhật trạng thái nút
        btnPrevious.setEnabled(currentQuestionIndex > 0);
        btnNext.setText(currentQuestionIndex == totalQuestions - 1 ? "Hoàn thành" : "Tiếp theo");

        if (timerEnabled) {
            startQuestionTimer();
        }
    }

    private void showTrueFalseOptions() {
        rgOptions.setVisibility(View.GONE);
        llTrueFalse.setVisibility(View.VISIBLE);
    }

    private void showMultipleChoiceOptions(Question question) {
        llTrueFalse.setVisibility(View.GONE);
        rgOptions.setVisibility(View.VISIBLE);

        rbOption1.setText(question.getOptionA());
        rbOption2.setText(question.getOptionB());
        rbOption3.setText(question.getOptionC());
        rbOption4.setText(question.getOptionD());
    }

    private void resetTrueFalseButtons() {
        btnTrue.setBackgroundColor(getResources().getColor(R.color.default_button));
        btnFalse.setBackgroundColor(getResources().getColor(R.color.default_button));
    }

    private void nextQuestion() {
        saveCurrentAnswer();

        if (currentQuestionIndex == totalQuestions - 1) {
            finishQuiz();
        } else {
            currentQuestionIndex++;
            displayQuestion();
        }
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion();
        }
    }

    private void saveCurrentAnswer() {
        userAnswers.set(currentQuestionIndex, selectedAnswer);
    }

    private void startQuestionTimer() {
        if (questionTimer != null) {
            questionTimer.cancel();
        }

        questionTimer = new CountDownTimer(timePerQuestion, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Thời gian: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Hết giờ!");
                nextQuestion();
            }
        }.start();
    }

    private void startTotalTimer() {
        totalTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                // Có thể hiển thị timer tổng ở đâu đó
            }

            @Override
            public void onFinish() {
                finishQuiz();
            }
        }.start();
    }

    private void finishQuiz() {
        saveCurrentAnswer();
        calculateScore();

        if (questionTimer != null) {
            questionTimer.cancel();
        }
        if (totalTimer != null) {
            totalTimer.cancel();
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", totalQuestions);
        startActivity(intent);
        finish();
    }

    private void calculateScore() {
        score = 0;
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            String userAnswer = userAnswers.get(i);

            if (userAnswer.equals(question.getCorrectAnswer())) {
                score++;
                if (soundEnabled && correctSound != null) {
                    correctSound.start();
                }
            } else {
                if (soundEnabled && incorrectSound != null) {
                    incorrectSound.start();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (questionTimer != null) {
            questionTimer.cancel();
        }
        if (totalTimer != null) {
            totalTimer.cancel();
        }
        if (backgroundMusic != null) {
            backgroundMusic.release();
        }
        if (correctSound != null) {
            correctSound.release();
        }
        if (incorrectSound != null) {
            incorrectSound.release();
        }
        if (database != null) {
            database.close();
        }
    }
}