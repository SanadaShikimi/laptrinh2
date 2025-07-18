package com.example.laptrinh2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laptrinh2.database.DatabaseHelper;
import com.example.laptrinh2.model.QuizResult;
import com.example.laptrinh2.adapter.HistoryAdapter;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private HistoryAdapter historyAdapter;

    // UI Components
    private Button btnBack, btnClearHistory;
    private ListView listViewHistory;
    private TextView tvUserName, tvTotalQuizzes, tvAverageScore, tvBestScore;
    private TextView tvEmptyHistory;

    // Constants
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize UI components
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Load history data
        loadHistoryData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnClearHistory = findViewById(R.id.btn_clear_history);
        listViewHistory = findViewById(R.id.list_view_history);
        tvUserName = findViewById(R.id.tv_user_name);
        tvTotalQuizzes = findViewById(R.id.tv_total_quizzes);
        tvAverageScore = findViewById(R.id.tv_average_score);
        tvBestScore = findViewById(R.id.tv_best_score);
        tvEmptyHistory = findViewById(R.id.tv_empty_history);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryDialog();
            }
        });
    }

    private void loadHistoryData() {
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set username
        tvUserName.setText("Lịch sử của " + username);

        // Get history data from database
        List<QuizResult> historyList = databaseHelper.getQuizHistory(userId);

        if (historyList.isEmpty()) {
            // Show empty state
            showEmptyState();
        } else {
            // Hide empty state and show data
            hideEmptyState();

            // Setup adapter
            historyAdapter = new HistoryAdapter(this, historyList);
            listViewHistory.setAdapter(historyAdapter);

            // Calculate and display statistics
            displayStatistics(historyList);
        }
    }

    private void displayStatistics(List<QuizResult> historyList) {
        int totalQuizzes = historyList.size();
        double totalScore = 0;
        int bestScore = 0;

        for (QuizResult result : historyList) {
            totalScore += result.getScore();
            if (result.getScore() > bestScore) {
                bestScore = result.getScore();
            }
        }

        double averageScore = totalScore / totalQuizzes;

        // Display statistics
        tvTotalQuizzes.setText("Tổng số lần làm bài: " + totalQuizzes);
        tvAverageScore.setText(String.format("Điểm trung bình: %.1f%%", averageScore));
        tvBestScore.setText("Điểm cao nhất: " + bestScore + "%");
    }

    private void showEmptyState() {
        tvEmptyHistory.setVisibility(View.VISIBLE);
        listViewHistory.setVisibility(View.GONE);
        btnClearHistory.setVisibility(View.GONE);

        // Hide statistics
        tvTotalQuizzes.setText("Tổng số lần làm bài: 0");
        tvAverageScore.setText("Điểm trung bình: 0%");
        tvBestScore.setText("Điểm cao nhất: 0%");
    }

    private void hideEmptyState() {
        tvEmptyHistory.setVisibility(View.GONE);
        listViewHistory.setVisibility(View.VISIBLE);
        btnClearHistory.setVisibility(View.VISIBLE);
    }

    private void showClearHistoryDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử làm bài?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    clearHistory();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void clearHistory() {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (userId != -1) {
            boolean success = databaseHelper.clearUserHistory(userId);

            if (success) {
                Toast.makeText(this, "Đã xóa lịch sử thành công", Toast.LENGTH_SHORT).show();
                loadHistoryData(); // Reload data
            } else {
                Toast.makeText(this, "Lỗi khi xóa lịch sử", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}