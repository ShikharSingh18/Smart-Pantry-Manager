package com.shikharsingh.smartpantrymanager;

import java.util.List;

/** Simple data model representing one recipe and what it needs to cook. */
public class Recipe {
    private long id;
    private String name;
    private List<String> ingredients;
    private String steps;

    public Recipe(long id, String name, List<String> ingredients, String steps) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public List<String> getIngredients() { return ingredients; }
    public String getSteps() { return steps; }
}