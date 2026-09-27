package com.shikharsingh.smartpantrymanager;

import java.util.List;

/**
 * A single recipe: a name, its list of required ingredients (each with a
 * quantity and unit), and its preparation steps. Ingredients now carry
 * quantity/unit data so IngredientMatcher can enforce the brief's
 * "in at least the required quantity" rule, not just presence
 */
public class Recipe {

    private final long id;
    private final String name;
    private final List<RecipeIngredient> ingredients;
    private final String steps;

    public Recipe(long id, String name, List<RecipeIngredient> ingredients, String steps) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    // Ingredient list for the Recipe Detail screen, e.g. "2 pieces tomato, 1 clove garlic"
    public String getIngredientsDisplayString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(ingredients.get(i).toString());
        }
        return sb.toString();
    }
}