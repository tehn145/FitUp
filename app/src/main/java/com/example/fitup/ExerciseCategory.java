package com.example.fitup;

public class ExerciseCategory {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private int iconResId;
    private int exerciseCount;

    public ExerciseCategory() {}

    public ExerciseCategory(String id, String name, String description, int iconResId, int exerciseCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
        this.exerciseCount = exerciseCount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public int getExerciseCount() { return exerciseCount; }
    public void setExerciseCount(int count) { this.exerciseCount = count; }
}