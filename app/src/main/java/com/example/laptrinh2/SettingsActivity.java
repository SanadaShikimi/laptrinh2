// SettingsActivity.java
package com.example.laptrinh2;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchBackgroundMusic, switchSoundEffects, switchTimer, switchTotalTimer;
    private SeekBar seekBarQuestionCount;
    private TextView tvQuestionCount;
    private Spinner spinnerAvatar;
    private Button btnSave, btnReset;

    private SQLiteDatabase database;
    private SharedPreferences sharedPreferences;
    private int userId;

    // Avatar options
    private String[] avatarOptions = {"default", "student", "teacher", "developer", "gamer"};
    private String[] avatarDisplayNames = {"Mặc định", "Sinh viên", "Giáo viên", "Lập trình viên", "Gamer"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();
        initViews();
        initDatabase();
        loadCurrentSettings();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cài đặt");
    }

    private void initViews() {
        switchBackgroundMusic = findViewById(R.id.switchBackgroundMusic);
        switchSoundEffects = findViewById(R.id.switchSoundEffects);
        switchTimer = findViewById(R.id.switchTimer);
        switchTotalTimer = findViewById(R.id.switchTotalTimer);
        seekBarQuestionCount = findViewById(R.id.seekBarQuestionCount);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        spinnerAvatar = findViewById(R.id.spinnerAvatar);
        btnSave = findViewById(R.id.btnSave);
        btnReset = findViewById(R.id.btnReset);
    }

    private void initDatabase() {
        database = openOrCreateDatabase("QuizApp.db", MODE_PRIVATE, null);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Setup avatar spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, avatarDisplayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvatar.setAdapter(adapter);
    }

    private void loadCurrentSettings() {
        String query = "SELECT * FROM settings WHERE user_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            // Load settings from database
            boolean backgroundMusic = cursor.getInt(cursor.getColumnIndex("background_music")) == 1;
            boolean soundEffects = cursor.getInt(cursor.getColumnIndex("sound_effects")) == 1;
            boolean timer = cursor.getInt(cursor.getColumnIndex("timer")) == 1;
            boolean totalTimer = cursor.getInt(cursor.getColumnIndex("total_timer")) == 1;
            int questionCount = cursor.getInt(cursor.getColumnIndex("question_count"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));

            // Set UI components
            switchBackgroundMusic.setChecked(backgroundMusic);
            switchSoundEffects.setChecked(soundEffects);
            switchTimer.setChecked(timer);
            switchTotalTimer.setChecked(totalTimer);
            seekBarQuestionCount.setProgress(questionCount);
            tvQuestionCount.setText(questionCount + " câu");

            // Set avatar spinner
            for (int i = 0; i < avatarOptions.length; i++) {
                if (avatarOptions[i].equals(avatar)) {
                    spinnerAvatar.setSelection(i);
                    break;
                }
            }
        } else {
            // Create default settings if not exist
            createDefaultSettings();
        }
        cursor.close();
    }

    private void createDefaultSettings() {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("background_music", 0);
        values.put("sound_effects", 0);
        values.put("timer", 0);
        values.put("total_timer", 0);
        values.put("question_count", 5);
        values.put("avatar", "default");

        database.insert("settings", null, values);

        // Set default UI
        switchBackgroundMusic.setChecked(false);
        switchSoundEffects.setChecked(false);
        switchTimer.setChecked(false);
        switchTotalTimer.setChecked(false);
        seekBarQuestionCount.setProgress(5);
        tvQuestionCount.setText("5 câu");
        spinnerAvatar.setSelection(0);
    }

    private void setupListeners() {
        // SeekBar listener for question count
        seekBarQuestionCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                if (progress > 20) progress = 20;
                tvQuestionCount.setText(progress + " câu");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save button listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        // Reset button listener
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetToDefaults();
            }
        });

        // Back button listener
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void saveSettings() {
        ContentValues values = new ContentValues();
        values.put("background_music", switchBackgroundMusic.isChecked() ? 1 : 0);
        values.put("sound_effects", switchSoundEffects.isChecked() ? 1 : 0);
        values.put("timer", switchTimer.isChecked() ? 1 : 0);
        values.put("total_timer", switchTotalTimer.isChecked() ? 1 : 0);
        values.put("question_count", seekBarQuestionCount.getProgress());
        values.put("avatar", avatarOptions[spinnerAvatar.getSelectedItemPosition()]);

        int rowsAffected = database.update("settings", values, "user_id = ?",
                new String[]{String.valueOf(userId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Cài đặt đã được lưu thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi lưu cài đặt", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetToDefaults() {
        switchBackgroundMusic.setChecked(false);
        switchSoundEffects.setChecked(false);
        switchTimer.setChecked(false);
        switchTotalTimer.setChecked(false);
        seekBarQuestionCount.setProgress(5);
        tvQuestionCount.setText("5 câu");
        spinnerAvatar.setSelection(0);

        Toast.makeText(this, "Đã khôi phục cài đặt mặc định", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}