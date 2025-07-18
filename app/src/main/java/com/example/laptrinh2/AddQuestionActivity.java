// AddQuestionActivity.java - Chức năng 4: Xây dựng câu hỏi trắc nghiệm
package com.example.laptrinh2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laptrinh2.database.DatabaseHelper;

public class AddQuestionActivity extends AppCompatActivity {
    private EditText etQuestion;
    private RadioGroup rgQuestionType;
    private RadioButton rbTrueFalse, rbMultipleChoice;
    private LinearLayout llTrueFalse, llMultipleChoice;
    private RadioGroup rgTrueFalse;
    private RadioButton rbTrue, rbFalse;
    private EditText etOptionA, etOptionB, etOptionC, etOptionD;
    private RadioGroup rgCorrectAnswer;
    private RadioButton rbCorrectA, rbCorrectB, rbCorrectC, rbCorrectD;
    private Spinner spinnerCategory, spinnerDifficulty;
    private Button btnSave, btnCancel, btnClear;
    private Toolbar toolbar;

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        initViews();
        setupToolbar();
        setupListeners();
        initDatabase();
    }

    private void initViews() {
        etQuestion = findViewById(R.id.etQuestion);
        rgQuestionType = findViewById(R.id.rgQuestionType);
        rbTrueFalse = findViewById(R.id.rbTrueFalse);
        rbMultipleChoice = findViewById(R.id.rbMultipleChoice);
        llTrueFalse = findViewById(R.id.llTrueFalse);
        llMultipleChoice = findViewById(R.id.llMultipleChoice);
        rgTrueFalse = findViewById(R.id.rgTrueFalse);
        rbTrue = findViewById(R.id.rbTrue);
        rbFalse = findViewById(R.id.rbFalse);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        rgCorrectAnswer = findViewById(R.id.rgCorrectAnswer);
        rbCorrectA = findViewById(R.id.rbCorrectA);
        rbCorrectB = findViewById(R.id.rbCorrectB);
        rbCorrectC = findViewById(R.id.rbCorrectC);
        rbCorrectD = findViewById(R.id.rbCorrectD);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnClear = findViewById(R.id.btnClear);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm câu hỏi mới");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupListeners() {
        // Listener cho loại câu hỏi
        rgQuestionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTrueFalse) {
                showTrueFalseOptions();
            } else if (checkedId == R.id.rbMultipleChoice) {
                showMultipleChoiceOptions();
            }
        });

        // Listener cho các nút
        btnSave.setOnClickListener(v -> saveQuestion());
        btnCancel.setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> clearForm());

        // Mặc định chọn True/False
        rbTrueFalse.setChecked(true);
        showTrueFalseOptions();
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
    }

    private void showTrueFalseOptions() {
        llTrueFalse.setVisibility(View.VISIBLE);
        llMultipleChoice.setVisibility(View.GONE);
    }

    private void showMultipleChoiceOptions() {
        llTrueFalse.setVisibility(View.GONE);
        llMultipleChoice.setVisibility(View.VISIBLE);
    }

    private void saveQuestion() {
        if (!validateInput()) {
            return;
        }

        String questionText = etQuestion.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        ContentValues values = new ContentValues();
        values.put("question_text", questionText);
        values.put("category", category);
        values.put("difficulty", difficulty);

        if (rbTrueFalse.isChecked()) {
            // Câu hỏi True/False
            values.put("question_type", 1);
            values.put("option_a", "True");
            values.put("option_b", "False");
            values.put("option_c", "");
            values.put("option_d", "");

            String correctAnswer = rgTrueFalse.getCheckedRadioButtonId() == R.id.rbTrue ? "True" : "False";
            values.put("correct_answer", correctAnswer);
        } else {
            // Câu hỏi Multiple Choice
            values.put("question_type", 2);
            values.put("option_a", etOptionA.getText().toString().trim());
            values.put("option_b", etOptionB.getText().toString().trim());
            values.put("option_c", etOptionC.getText().toString().trim());
            values.put("option_d", etOptionD.getText().toString().trim());

            String correctAnswer = getSelectedCorrectAnswer();
            values.put("correct_answer", correctAnswer);
        }

        long result = database.insert("questions", null, values);

        if (result != -1) {
            Toast.makeText(this, "Câu hỏi đã được thêm thành công!", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(this, "Lỗi khi thêm câu hỏi!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        String questionText = etQuestion.getText().toString().trim();

        if (questionText.isEmpty()) {
            etQuestion.setError("Vui lòng nhập câu hỏi");
            etQuestion.requestFocus();
            return false;
        }

        if (rbTrueFalse.isChecked()) {
            if (rgTrueFalse.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Vui lòng chọn đáp án đúng", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (rbMultipleChoice.isChecked()) {
            if (etOptionA.getText().toString().trim().isEmpty() ||
                    etOptionB.getText().toString().trim().isEmpty() ||
                    etOptionC.getText().toString().trim().isEmpty() ||
                    etOptionD.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ các lựa chọn", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (rgCorrectAnswer.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Vui lòng chọn đáp án đúng", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Vui lòng chọn loại câu hỏi", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getSelectedCorrectAnswer() {
        int selectedId = rgCorrectAnswer.getCheckedRadioButtonId();

        if (selectedId == R.id.rbCorrectA) {
            return etOptionA.getText().toString().trim();
        } else if (selectedId == R.id.rbCorrectB) {
            return etOptionB.getText().toString().trim();
        } else if (selectedId == R.id.rbCorrectC) {
            return etOptionC.getText().toString().trim();
        } else if (selectedId == R.id.rbCorrectD) {
            return etOptionD.getText().toString().trim();
        }

        return "";
    }

    private void clearForm() {
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        rgQuestionType.clearCheck();
        rgTrueFalse.clearCheck();
        rgCorrectAnswer.clearCheck();
        spinnerCategory.setSelection(0);
        spinnerDifficulty.setSelection(0);

        // Reset về mặc định
        rbTrueFalse.setChecked(true);
        showTrueFalseOptions();
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