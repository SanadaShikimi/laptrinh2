package com.example.laptrinh2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laptrinh2.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // UI Components
    private Button btnLogin, btnStartQuiz, btnSettings, btnCreateQuestion, btnHistory;
    private LinearLayout userInfoSection;
    private ImageView userAvatar;
    private TextView userName, userStats;

    // Constants
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize UI components
        initViews();

        // Setup click listeners
        setupClickListeners();

        // Update UI based on login status
        updateUIForLoginStatus();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnStartQuiz = findViewById(R.id.btn_start_quiz);
        btnSettings = findViewById(R.id.btn_settings);
        btnCreateQuestion = findViewById(R.id.btn_create_question);
        btnHistory = findViewById(R.id.btn_history);

        userInfoSection = findViewById(R.id.user_info_section);
        userAvatar = findViewById(R.id.user_avatar);
        userName = findViewById(R.id.user_name);
        userStats = findViewById(R.id.user_stats);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    // User is logged in, show logout option
                    showLogoutDialog();
                } else {
                    // User is not logged in, go to login screen
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 100);
                }
            }
        });

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start quiz activity
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open settings activity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnCreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    // Open create question activity
                    Intent intent = new Intent(MainActivity.this, AddQuestionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Vui lòng đăng nhập để tạo câu hỏi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    // Open history activity
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Vui lòng đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUIForLoginStatus() {
        if (isUserLoggedIn()) {
            // User is logged in
            String username = sharedPreferences.getString(KEY_USERNAME, "");

            btnLogin.setText("🚪 Đăng xuất");
            userName.setText("Xin chào, " + username);
            userStats.setText("Nhấn để xem thống kê chi tiết");
            userInfoSection.setVisibility(View.VISIBLE);

            // Enable all features
            btnCreateQuestion.setEnabled(true);
            btnHistory.setEnabled(true);

        } else {
            // User is not logged in
            btnLogin.setText("🔐 Đăng nhập");
            userName.setText("Chưa đăng nhập");
            userStats.setText("Hãy đăng nhập để lưu kết quả");
            userInfoSection.setVisibility(View.GONE);

            // Disable some features
            btnCreateQuestion.setEnabled(false);
            btnHistory.setEnabled(false);
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        // Clear shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Update UI
        updateUIForLoginStatus();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Login successful, update UI
            updateUIForLoginStatus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when returning from other activities
        updateUIForLoginStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}