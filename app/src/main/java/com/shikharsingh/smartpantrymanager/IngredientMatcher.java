package com.shikharsingh.smartpantrymanager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements "strict-matching rule": a recipe only qualifies
 * as suggested if EVERY one of its required ingredients is present in the
 * user's pantry. Includes basic normalisation around locales of English so "tomato" matches "tomatoes"
 * without needing full NLP.
 */
public class IngredientMatcher {

    /** Lowercases, trims, and strips a common trailing "es"/"s" plural so simple
     *  singular/plural mismatches don't break matching. Naive on purpose */
    public static String normalize(String ingredient) {
        String result = ingredient.trim().toLowerCase();
        if (result.endsWith("es")) {
            result = result.substring(0, result.length() - 2);
        } else if (result.endsWith("s") && result.length() > 3) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    // Returns true only if every ingredient the recipe needs is present in the pantry
    public static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        Set<String> pantryNormalized = new HashSet<>();
        for (PantryItem item : pantryItems) {
            pantryNormalized.add(normalize(item.getName()));
        }

        for (String requiredIngredient : recipe.getIngredients()) {
            if (!pantryNormalized.contains(normalize(requiredIngredient))) {
                return false; // missing even one ingredient disqualifies the recipe
            }
        }
        return true;
    }

    // Optional stretch goal, recipes missing exactly one ingredient
    public static boolean isAlmostThere(Recipe recipe, List<PantryItem> pantryItems) {
        Set<String> pantryNormalized = new HashSet<>();
        for (PantryItem item : pantryItems) {
            pantryNormalized.add(normalize(item.getName()));
        }

        int missingCount = 0;
        for (String requiredIngredient : recipe.getIngredients()) {
            if (!pantryNormalized.contains(normalize(requiredIngredient))) {
                missingCount++;
            }
        }
        return missingCount == 1;
    }
}