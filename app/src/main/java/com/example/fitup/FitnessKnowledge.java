package com.example.fitup;

import java.util.List;

public class FitnessKnowledge {
    private String content;
    private List<String> keywords;

    public FitnessKnowledge() {}

    public FitnessKnowledge(String content, List<String> keywords) {
        this.content = content;
        this.keywords = keywords;
    }

    public String getContent() { return content; }
    public List<String> getKeywords() { return keywords; }
}