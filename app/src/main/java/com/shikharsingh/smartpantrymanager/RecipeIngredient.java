package com.shikharsingh.smartpantrymanager;

/**
 * A single ingredient requirement inside a recipe: a name, the quantity
 * needed, and the unit that quantity is measured in (e.g. "tomato", 2,
 * "pieces"). Used by IngredientMatcher to enforce the strict-matching
 * rule against the user's pantry.
 */
public class RecipeIngredient {

    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return quantity + " " + unit + " " + name;
    }
}