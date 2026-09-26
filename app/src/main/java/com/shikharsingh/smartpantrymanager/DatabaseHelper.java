package com.shikharsingh.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Handles all local persistence for Smart Pantry Manager using SQLite.
// Uses two tables: pantry_items (user's current ingredients) and recipes (a fixed seeded collection, plus their required ingredients as a delimited string for simple strict-matching)
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INGREDIENTS = "ingredients"; // comma-separated
    public static final String COL_RECIPE_STEPS = "steps";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QUANTITY + " REAL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // Populate at least 15-20 recipes on first run
    private void seedRecipes(SQLiteDatabase db) {
        addSeedRecipe(db, "Tomato Pasta", "pasta,tomato,garlic,olive oil,salt",
                "1. Boil pasta.\n2. Fry garlic in oil.\n3. Add chopped tomato, simmer.\n4. Toss with pasta and salt.");
        addSeedRecipe(db, "Scrambled Eggs", "egg,butter,salt,pepper",
                "1. Whisk eggs.\n2. Melt butter in pan.\n3. Pour eggs, stir until set.\n4. Season.");
        addSeedRecipe(db, "Vegetable Stir Fry", "carrot,broccoli,soy sauce,garlic,oil",
                "1. Heat oil.\n2. Fry garlic.\n3. Add chopped vegetables, stir fry.\n4. Add soy sauce, cook 3 min.");
        addSeedRecipe(db, "Cheese Toast", "bread,cheese,butter",
                "1. Butter bread.\n2. Add cheese slice.\n3. Grill/toast until melted.");
        addSeedRecipe(db, "Rice and Beans", "rice,beans,onion,garlic,salt",
                "1. Cook rice.\n2. Fry onion and garlic.\n3. Add beans, salt, simmer 5 min.\n4. Serve over rice.");
        addSeedRecipe(db, "Chicken Soup", "chicken,carrot,onion,celery,salt,water",
                "1. Boil chicken in water.\n2. Add chopped vegetables.\n3. Simmer 20 min.\n4. Season with salt.");
        addSeedRecipe(db, "Pancakes", "flour,egg,milk,sugar,butter",
                "1. Mix flour, egg, milk, sugar into batter.\n2. Melt butter in pan.\n3. Pour batter, cook both sides.");
        addSeedRecipe(db, "Fruit Salad", "apple,banana,orange,sugar",
                "1. Chop all fruit.\n2. Mix in bowl.\n3. Sprinkle sugar, toss, chill.");
        addSeedRecipe(db, "Garlic Butter Mushrooms", "mushroom,butter,garlic,salt",
                "1. Melt butter.\n2. Fry garlic.\n3. Add mushrooms, cook until soft.\n4. Season with salt.");
        addSeedRecipe(db, "Tuna Sandwich", "bread,tuna,mayonnaise,lettuce",
                "1. Mix tuna with mayonnaise.\n2. Spread on bread.\n3. Add lettuce, close sandwich.");
        addSeedRecipe(db, "Omelette", "egg,cheese,onion,salt,pepper",
                "1. Whisk eggs with salt and pepper.\n2. Pour into pan.\n3. Add cheese and onion.\n4. Fold and serve.");
        addSeedRecipe(db, "Potato Wedges", "potato,oil,salt,pepper",
                "1. Cut potato into wedges.\n2. Coat in oil, salt, pepper.\n3. Bake or fry until golden.");
        addSeedRecipe(db, "Tomato Soup", "tomato,onion,garlic,salt,water",
                "1. Fry onion and garlic.\n2. Add chopped tomato and water.\n3. Simmer 15 min.\n4. Blend and season.");
        addSeedRecipe(db, "Banana Smoothie", "banana,milk,sugar",
                "1. Blend banana, milk and sugar together.\n2. Serve chilled.");
        addSeedRecipe(db, "Fried Rice", "rice,egg,carrot,soy sauce,oil",
                "1. Heat oil.\n2. Scramble egg in pan.\n3. Add rice and chopped carrot.\n4. Add soy sauce, stir fry.");
        addSeedRecipe(db, "Grilled Cheese Quesadilla", "tortilla,cheese,butter",
                "1. Place cheese between two tortillas.\n2. Butter outside.\n3. Grill both sides until golden.");
    }

    private void addSeedRecipe(SQLiteDatabase db, String name, String ingredients, String steps) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_RECIPE_INGREDIENTS, ingredients);
        values.put(COL_RECIPE_STEPS, steps);
        db.insert(TABLE_RECIPES, null, values);
    }

    // Pantry CRUD

    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, name);
        values.put(COL_PANTRY_QUANTITY, quantity);
        values.put(COL_PANTRY_UNIT, unit);
        values.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.insert(TABLE_PANTRY, null, values);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_PANTRY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY))
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, name);
        values.put(COL_PANTRY_QUANTITY, quantity);
        values.put(COL_PANTRY_UNIT, unit);
        values.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.update(TABLE_PANTRY, values, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Recipe Read

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                String ingredientsRaw = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INGREDIENTS));
                List<String> ingredientList = new ArrayList<>();
                for (String ing : ingredientsRaw.split(",")) {
                    ingredientList.add(ing.trim());
                }
                Recipe recipe = new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                        ingredientList,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS))
                );
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }
}
