package com.shikharsingh.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the strict-matching rule (see IngredientMatcher) against the user's
 * current pantry and shows only recipes where every required ingredient
 * is present. Shows a message instead of a blank screen if none match.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Suggested Recipes");

        dbHelper = new DatabaseHelper(this);
        loadSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions(); // pantry may have changed since this screen last opened
    }

    private void loadSuggestions() {
        List<PantryItem> pantryItems = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        List<Recipe> matchedRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (IngredientMatcher.canMakeRecipe(recipe, pantryItems)) {
                matchedRecipes.add(recipe);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRecipes);
        TextView emptyState = findViewById(R.id.textEmptyState);

        if (matchedRecipes.isEmpty()) {
            recyclerView.setVisibility(android.view.View.GONE);
            emptyState.setVisibility(android.view.View.VISIBLE);
            emptyState.setText("No recipes match your pantry yet. Add more ingredients.");
        } else {
            recyclerView.setVisibility(android.view.View.VISIBLE);
            emptyState.setVisibility(android.view.View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new RecipeAdapter(matchedRecipes, this));
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }
}