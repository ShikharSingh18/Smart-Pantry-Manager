package com.shikharsingh.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Pantry List screen: the app's home screen. Shows every ingredient the
 * user currently has, with edit/delete actions, plus navigation to
 * Suggested Recipes and Settings via the toolbar menu, and an Add button.
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private PantryAdapter adapter;
    private List<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fabAddItem).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems(); // refresh in case items were added/edited on another screen
    }

    private void loadPantryItems() {
        pantryItems = dbHelper.getAllPantryItems();
        adapter = new PantryAdapter(pantryItems, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("pantry_id", item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_suggested_recipes) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}