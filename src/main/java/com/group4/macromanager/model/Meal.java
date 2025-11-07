package com.group4.macromanager.model;

import java.util.List;

public class Meal {
    private String id;
    private String name;
    private String mealType;
    private String notes;
    private List<Food> foods;
    private boolean isFavorite;
    private String imageUrl;

    public Meal(String id, String name, String mealType, String notes, List<Food> foods, boolean isFavorite, String imageUrl) {
        this.id = id;
        this.name = name;
        this.mealType = mealType;
        this.notes = notes;
        this.foods = foods;
        this.isFavorite = isFavorite;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Food> getFoods() { return foods; }
    public void setFoods(List<Food> foods) { this.foods = foods; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
