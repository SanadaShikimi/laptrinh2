package com.example.laptrinh2.model;

import java.io.Serializable;

public class Question implements Serializable {
    private int questionId;
    private String questionText;
    private String questionType; // "true_false" or "multiple_choice"
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer; // A, B, C, D for multiple choice; A, B for true/false
    private String category;
    private String difficulty;

    // Constructors
    public Question() {}

    // Constructor for True/False questions
    public Question(String questionText, String optionA, String optionB,
                    String correctAnswer, String category) {
        this.questionText = questionText;
        this.questionType = "true_false";
        this.optionA = optionA;
        this.optionB = optionB;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = "Easy";
    }

    // Constructor for Multiple Choice questions
    public Question(String questionText, String optionA, String optionB,
                    String optionC, String optionD, String correctAnswer, String category) {
        this.questionText = questionText;
        this.questionType = "multiple_choice";
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = "Easy";
    }

    // Full constructor
    public Question(int questionId, String questionText, String questionType,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctAnswer, String category, String difficulty) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // Utility methods
    public boolean isTrueFalse() {
        return "true_false".equals(questionType);
    }

    public boolean isMultipleChoice() {
        return "multiple_choice".equals(questionType);
    }

    public String[] getOptions() {
        if (isTrueFalse()) {
            return new String[]{optionA, optionB};
        } else {
            return new String[]{optionA, optionB, optionC, optionD};
        }
    }

    public boolean isCorrectAnswer(String userAnswer) {
        return correctAnswer != null && correctAnswer.equals(userAnswer);
    }

    public String getCorrectAnswerText() {
        switch (correctAnswer) {
            case "A":
                return optionA;
            case "B":
                return optionB;
            case "C":
                return optionC;
            case "D":
                return optionD;
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionText='" + questionText + '\'' +
                ", questionType='" + questionType + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}