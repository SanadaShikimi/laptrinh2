package com.example.laptrinh2.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuizResult {
    private int id;
    private int userId;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private long timeTaken; // in milliseconds
    private String date;
    private String category;

    // Constructors
    public QuizResult() {
    }

    public QuizResult(int userId, int score, int totalQuestions, int correctAnswers,
                      long timeTaken, String category) {
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.timeTaken = timeTaken;
        this.category = category;
        this.date = getCurrentDate();
    }

    public QuizResult(int id, int userId, int score, int totalQuestions, int correctAnswers,
                      long timeTaken, String date, String category) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.timeTaken = timeTaken;
        this.date = date;
        this.category = category;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Helper methods
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getFormattedTime() {
        long seconds = timeTaken / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%d phút %d giây", minutes, seconds);
        } else {
            return String.format("%d giây", seconds);
        }
    }

    public String getScoreText() {
        return score + "%";
    }

    public String getResultText() {
        return correctAnswers + "/" + totalQuestions + " câu đúng";
    }

    public String getPerformanceLevel() {
        if (score >= 90) {
            return "Xuất sắc";
        } else if (score >= 80) {
            return "Giỏi";
        } else if (score >= 70) {
            return "Khá";
        } else if (score >= 60) {
            return "Trung bình";
        } else {
            return "Yếu";
        }
    }
}