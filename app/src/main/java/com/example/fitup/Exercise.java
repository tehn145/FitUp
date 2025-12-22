package com.example.fitup;

import java.util.List;

public class Exercise {
    private String id;
    private String name;
    private String categoryId;
    private String description;
    private String difficulty; // beginner, intermediate, advanced
    private String targetMuscle;
    private String equipment; // bodyweight, dumbbell, barbell, etc.
    private String videoUrl;
    private String gifUrl;
    private List<String> instructions;
    private List<String> tips;
    private int defaultReps;
    private int defaultSets;

    public Exercise() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTargetMuscle() { return targetMuscle; }
    public void setTargetMuscle(String targetMuscle) { this.targetMuscle = targetMuscle; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }

    public List<String> getTips() { return tips; }
    public void setTips(List<String> tips) { this.tips = tips; }

    public int getDefaultReps() { return defaultReps; }
    public void setDefaultReps(int reps) { this.defaultReps = reps; }

    public int getDefaultSets() { return defaultSets; }
    public void setDefaultSets(int sets) { this.defaultSets = sets; }
}