package com.shikharsingh.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// Shows the full ingredient list and preparation steps for one selected recipe
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long recipeId = getIntent().getLongExtra("recipe_id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        TextView textName = findViewById(R.id.textRecipeDetailName);
        TextView textIngredients = findViewById(R.id.textRecipeDetailIngredients);
        TextView textSteps = findViewById(R.id.textRecipeDetailSteps);

        for (Recipe recipe : allRecipes) {
            if (recipe.getId() == recipeId) {
                setTitle(recipe.getName());
                textName.setText(recipe.getName());
                textIngredients.setText("Ingredients: " + String.join(", ", recipe.getIngredients()));
                textSteps.setText(recipe.getSteps());
                break;
            }
        }
    }
}