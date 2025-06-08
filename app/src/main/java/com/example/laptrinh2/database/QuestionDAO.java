package com.example.laptrinh2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.laptrinh2.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public QuestionDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // Insert new question
    public long insertQuestion(Question question) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_QUESTION_TEXT, question.getQuestionText());
        values.put(DatabaseHelper.COLUMN_QUESTION_TYPE, question.getQuestionType());
        values.put(DatabaseHelper.COLUMN_OPTION_A, question.getOptionA());
        values.put(DatabaseHelper.COLUMN_OPTION_B, question.getOptionB());
        values.put(DatabaseHelper.COLUMN_OPTION_C, question.getOptionC());
        values.put(DatabaseHelper.COLUMN_OPTION_D, question.getOptionD());
        values.put(DatabaseHelper.COLUMN_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(DatabaseHelper.COLUMN_CATEGORY, question.getCategory());
        values.put(DatabaseHelper.COLUMN_DIFFICULTY, question.getDifficulty());

        return database.insert(DatabaseHelper.TABLE_QUESTIONS, null, values);
    }

    // Get all questions
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_QUESTIONS;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = cursorToQuestion(cursor);
                questions.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questions;
    }

    // Get questions by category
    public List<Question> getQuestionsByCategory(String category) {
        List<Question> questions = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_QUESTIONS +
                " WHERE " + DatabaseHelper.COLUMN_CATEGORY + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                Question question = cursorToQuestion(cursor);
                questions.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questions;
    }

    // Get questions by difficulty
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        List<Question> questions = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_QUESTIONS +
                " WHERE " + DatabaseHelper.COLUMN_DIFFICULTY + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{difficulty});

        if (cursor.moveToFirst()) {
            do {
                Question question = cursorToQuestion(cursor);
                questions.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questions;
    }

    // Get random questions for quiz
    public List<Question> getRandomQuestions(int count) {
        List<Question> allQuestions = getAllQuestions();
        Collections.shuffle(allQuestions);

        if (allQuestions.size() <= count) {
            return allQuestions;
        } else {
            return allQuestions.subList(0, count);
        }
    }

    // Get random questions by category
    public List<Question> getRandomQuestionsByCategory(String category, int count) {
        List<Question> categoryQuestions = getQuestionsByCategory(category);
        Collections.shuffle(categoryQuestions);

        if (categoryQuestions.size() <= count) {
            return categoryQuestions;
        } else {
            return categoryQuestions.subList(0, count);
        }
    }

    // Get question by ID
    public Question getQuestionById(int questionId) {
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_QUESTIONS +
                " WHERE " + DatabaseHelper.COLUMN_QUESTION_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(questionId)});

        Question question = null;
        if (cursor.moveToFirst()) {
            question = cursorToQuestion(cursor);
        }

        cursor.close();
        return question;
    }

    // Update question
    public int updateQuestion(Question question) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_QUESTION_TEXT, question.getQuestionText());
        values.put(DatabaseHelper.COLUMN_QUESTION_TYPE, question.getQuestionType());
        values.put(DatabaseHelper.COLUMN_OPTION_A, question.getOptionA());
        values.put(DatabaseHelper.COLUMN_OPTION_B, question.getOptionB());
        values.put(DatabaseHelper.COLUMN_OPTION_C, question.getOptionC());
        values.put(DatabaseHelper.COLUMN_OPTION_D, question.getOptionD());
        values.put(DatabaseHelper.COLUMN_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(DatabaseHelper.COLUMN_CATEGORY, question.getCategory());
        values.put(DatabaseHelper.COLUMN_DIFFICULTY, question.getDifficulty());

        return database.update(DatabaseHelper.TABLE_QUESTIONS, values,
                DatabaseHelper.COLUMN_QUESTION_ID + " = ?",
                new String[]{String.valueOf(question.getQuestionId())});
    }

    // Delete question
    public int deleteQuestion(int questionId) {
        return database.delete(DatabaseHelper.TABLE_QUESTIONS,
                DatabaseHelper.COLUMN_QUESTION_ID + " = ?",
                new String[]{String.valueOf(questionId)});
    }

    // Get all categories
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + DatabaseHelper.COLUMN_CATEGORY +
                " FROM " + DatabaseHelper.TABLE_QUESTIONS;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categories;
    }

    // Get question count by category
    public int getQuestionCountByCategory(String category) {
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_QUESTIONS +
                " WHERE " + DatabaseHelper.COLUMN_CATEGORY + " = ?";
        Cursor cursor = database.rawQuery(countQuery, new String[]{category});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    // Get total question count
    public int getTotalQuestionCount() {
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_QUESTIONS;
        Cursor cursor = database.rawQuery(countQuery, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    // Helper method to convert cursor to Question object
    private Question cursorToQuestion(Cursor cursor) {
        Question question = new Question();

        question.setQuestionId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_ID)));
        question.setQuestionText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_TEXT)));
        question.setQuestionType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_TYPE)));
        question.setOptionA(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OPTION_A)));
        question.setOptionB(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OPTION_B)));
        question.setOptionC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OPTION_C)));
        question.setOptionD(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OPTION_D)));
        question.setCorrectAnswer(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CORRECT_ANSWER)));
        question.setCategory(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY)));
        question.setDifficulty(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DIFFICULTY)));

        return question;
    }
}