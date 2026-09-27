package com.shikharsingh.smartpantrymanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the strict-matching rule: a recipe only qualifies
 * as "suggested" if every single required ingredient is present in the
 * user's pantry, in at least the required quantity.
 *
 * Ingredient names are normalised (case, whitespace, naive singular/plural
 * stripping) so "tomato" and "tomatoes" are treated as the same ingredient,
 * per the assignment brief's explicit example. Units are normalised through
 * a small synonym map (e.g. "g"/"gram"/"grams" all resolve the same way) so
 * quantity comparisons work even when the user typed a slightly different
 * unit string than the recipe uses.
 *
 * Limitation: true cross-unit conversion (e.g.
 * grams to pieces) is out of scope for this assignment. If a pantry item's
 * unit cannot be reconciled with the recipe's required unit after
 * normalisation, the match falls back to presence-only for that ingredient
 * rather than failing outright - a reasonable trade-off
 */
public class IngredientMatcher {

    private static final Map<String, String> UNIT_SYNONYMS = new HashMap<>();
    static {
        UNIT_SYNONYMS.put("g", "gram");
        UNIT_SYNONYMS.put("gr", "gram");
        UNIT_SYNONYMS.put("kg", "kilogram");
        UNIT_SYNONYMS.put("ml", "milliliter");
        UNIT_SYNONYMS.put("l", "liter");
        UNIT_SYNONYMS.put("tbs", "tablespoon");
        UNIT_SYNONYMS.put("tbsp", "tablespoon");
        UNIT_SYNONYMS.put("tsp", "teaspoon");
        UNIT_SYNONYMS.put("pc", "piece");
        UNIT_SYNONYMS.put("pcs", "piece");
    }

    // Returns true only if every ingredient the recipe needs is satisfied by the pantry
    public static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!pantryHasEnoughOf(required, pantryItems)) {
                return false; // strict rule: a single missing/insufficient ingredient disqualifies the whole recipe
            }
        }
        return true;
    }

    private static boolean pantryHasEnoughOf(RecipeIngredient required, List<PantryItem> pantryItems) {
        String requiredNameNormalized = normalizeName(required.getName());

        for (PantryItem pantryItem : pantryItems) {
            if (!normalizeName(pantryItem.getName()).equals(requiredNameNormalized)) {
                continue; // not the same ingredient
            }

            String requiredUnit = normalizeUnit(required.getUnit());
            String pantryUnit = normalizeUnit(pantryItem.getUnit());

            if (requiredUnit.equals(pantryUnit)) {
                // Same unit family - can do a genuine quantity comparison
                return pantryItem.getQuantity() >= required.getQuantity();
            } else {
                // Units don't reconcile (e.g. "pieces" vs "grams") - fall back
                // to presence-only rather than failing on an unconvertible unit
                return true;
            }
        }
        return false; // ingredient not found in pantry at all
    }

    /**
     * Lowercase, trim, and naive de-pluralize so "tomato"/"tomatoes",
     * "egg"/"eggs", "orange"/"oranges" etc. are treated as the same ingredient.
     */
    public static String normalizeName(String rawName) {
        if (rawName == null) return "";
        String name = rawName.trim().toLowerCase();

        if (name.endsWith("oes") && name.length() > 4) {
            name = name.substring(0, name.length() - 2); // "tomatoes" -> "tomato", "potatoes" -> "potato"
        } else if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 3) {
            name = name.substring(0, name.length() - 1); // "eggs" -> "egg", "oranges" -> "orange"
        }
        return name;
    }

    // Lowercase, trim, resolve common abbreviations/synonyms to one canonical unit string
    public static String normalizeUnit(String rawUnit) {
        if (rawUnit == null || rawUnit.trim().isEmpty()) return "";
        String unit = rawUnit.trim().toLowerCase();

        if (unit.endsWith("s") && unit.length() > 3) {
            unit = unit.substring(0, unit.length() - 1); // "pieces" -> "piece", "grams" -> "gram"
        }
        return UNIT_SYNONYMS.getOrDefault(unit, unit);
    }
}