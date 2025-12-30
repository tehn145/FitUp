package com.example.fitup;

import java.util.List;

public class QuizQuestion {
    private String id;
    private String category;
    private String questionText;
    private List<String> options;
    private int selectedOptionIndex = -1;

    public QuizQuestion(String id, String category, String questionText, List<String> options) {
        this.id = id;
        this.category = category;
        this.questionText = questionText;
        this.options = options;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getSelectedOptionIndex() { return selectedOptionIndex; }
    public void setSelectedOptionIndex(int selectedOptionIndex) { this.selectedOptionIndex = selectedOptionIndex; }
}