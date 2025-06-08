package com.example.laptrinh2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "QuizApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_TEST_HISTORY = "test_history";
    public static final String TABLE_TEST_ANSWERS = "test_answers";

    // User Table Columns
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_CREATED_DATE = "created_date";

    // Question Table Columns
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_QUESTION_TYPE = "question_type"; // 'true_false' or 'multiple_choice'
    public static final String COLUMN_OPTION_A = "option_a";
    public static final String COLUMN_OPTION_B = "option_b";
    public static final String COLUMN_OPTION_C = "option_c";
    public static final String COLUMN_OPTION_D = "option_d";
    public static final String COLUMN_CORRECT_ANSWER = "correct_answer"; // A, B, C, D hoặc TRUE, FALSE
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DIFFICULTY = "difficulty";

    // Test History Table Columns
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_HISTORY_USER_ID = "user_id";
    public static final String COLUMN_TEST_DATE = "test_date";
    public static final String COLUMN_TOTAL_QUESTIONS = "total_questions";
    public static final String COLUMN_CORRECT_ANSWERS = "correct_answers";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TIME_TAKEN = "time_taken"; // in seconds
    public static final String COLUMN_TEST_SETTINGS = "test_settings"; // JSON string

    // Test Answers Table Columns
    public static final String COLUMN_ANSWER_ID = "answer_id";
    public static final String COLUMN_ANSWER_HISTORY_ID = "history_id";
    public static final String COLUMN_ANSWER_QUESTION_ID = "question_id";
    public static final String COLUMN_USER_ANSWER = "user_answer";
    public static final String COLUMN_IS_CORRECT = "is_correct";

    // Create Users Table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_AVATAR + " TEXT, " +
                    COLUMN_CREATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    // Create Questions Table
    private static final String CREATE_TABLE_QUESTIONS =
            "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                    COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_QUESTION_TEXT + " TEXT NOT NULL, " +
                    COLUMN_QUESTION_TYPE + " TEXT NOT NULL CHECK(" + COLUMN_QUESTION_TYPE +
                    " IN ('true_false', 'multiple_choice')), " +
                    COLUMN_OPTION_A + " TEXT, " +
                    COLUMN_OPTION_B + " TEXT, " +
                    COLUMN_OPTION_C + " TEXT, " +
                    COLUMN_OPTION_D + " TEXT, " +
                    COLUMN_CORRECT_ANSWER + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT DEFAULT 'CNTT', " +
                    COLUMN_DIFFICULTY + " TEXT DEFAULT 'Easy' CHECK(" + COLUMN_DIFFICULTY +
                    " IN ('Easy', 'Medium', 'Hard'))" +
                    ");";

    // Create Test History Table
    private static final String CREATE_TABLE_TEST_HISTORY =
            "CREATE TABLE " + TABLE_TEST_HISTORY + " (" +
                    COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HISTORY_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_TEST_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_TOTAL_QUESTIONS + " INTEGER NOT NULL, " +
                    COLUMN_CORRECT_ANSWERS + " INTEGER NOT NULL, " +
                    COLUMN_SCORE + " REAL NOT NULL, " +
                    COLUMN_TIME_TAKEN + " INTEGER, " +
                    COLUMN_TEST_SETTINGS + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_HISTORY_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                    ");";

    // Create Test Answers Table
    private static final String CREATE_TABLE_TEST_ANSWERS =
            "CREATE TABLE " + TABLE_TEST_ANSWERS + " (" +
                    COLUMN_ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ANSWER_HISTORY_ID + " INTEGER NOT NULL, " +
                    COLUMN_ANSWER_QUESTION_ID + " INTEGER NOT NULL, " +
                    COLUMN_USER_ANSWER + " TEXT NOT NULL, " +
                    COLUMN_IS_CORRECT + " INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(" + COLUMN_ANSWER_HISTORY_ID + ") REFERENCES " +
                    TABLE_TEST_HISTORY + "(" + COLUMN_HISTORY_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_ANSWER_QUESTION_ID + ") REFERENCES " +
                    TABLE_QUESTIONS + "(" + COLUMN_QUESTION_ID + ")" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_QUESTIONS);
        db.execSQL(CREATE_TABLE_TEST_HISTORY);
        db.execSQL(CREATE_TABLE_TEST_ANSWERS);

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_ANSWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Sample user
        db.execSQL("INSERT INTO " + TABLE_USERS +
                " (username, password, email) VALUES ('admin', '123456', 'admin@quiz.com');");

        // Sample questions - True/False
        db.execSQL("INSERT INTO " + TABLE_QUESTIONS +
                " (question_text, question_type, option_a, option_b, correct_answer, category) VALUES " +
                "('Java là ngôn ngữ lập trình hướng đối tượng', 'true_false', 'Đúng', 'Sai', 'A', 'Java');");

        db.execSQL("INSERT INTO " + TABLE_QUESTIONS +
                " (question_text, question_type, option_a, option_b, correct_answer, category) VALUES " +
                "('HTML là ngôn ngữ lập trình', 'true_false', 'Đúng', 'Sai', 'B', 'Web');");

        // Sample questions - Multiple Choice
        db.execSQL("INSERT INTO " + TABLE_QUESTIONS +
                " (question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, category) VALUES " +
                "('Ngôn ngữ nào được sử dụng để phát triển ứng dụng Android?', 'multiple_choice', " +
                "'Java', 'Python', 'C++', 'JavaScript', 'A', 'Android');");

        db.execSQL("INSERT INTO " + TABLE_QUESTIONS +
                " (question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, category) VALUES " +
                "('CSS viết tắt của từ nào?', 'multiple_choice', " +
                "'Computer Style Sheets', 'Cascading Style Sheets', 'Creative Style Sheets', 'Colorful Style Sheets', 'B', 'Web');");

        db.execSQL("INSERT INTO " + TABLE_QUESTIONS +
                " (question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, category) VALUES " +
                "('Hệ quản trị cơ sở dữ liệu nào sau đây là mã nguồn mở?', 'multiple_choice', " +
                "'Oracle', 'SQL Server', 'MySQL', 'Access', 'C', 'Database');");
    }
}