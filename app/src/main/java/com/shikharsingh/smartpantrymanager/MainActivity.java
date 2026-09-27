package com.shikharsingh.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {
    // Threshold chosen for the  demo; adjustable without touching UI logic
    private static final int EXPIRING_SOON_DAYS = 3;

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private List<PantryItem> allPantryItems;
    private boolean showingExpiringOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fabAddItem).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditIngredientActivity.class)));

        findViewById(R.id.expiryBanner).setOnClickListener(v -> {
            showingExpiringOnly = !showingExpiringOnly;
            renderList();
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        allPantryItems = dbHelper.getAllPantryItems();
        renderList();
    }

    private void renderList() {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        boolean alertsEnabled = prefs.getBoolean(SettingsActivity.KEY_EXPIRY_ALERTS, true);

        List<PantryItem> expiringSoon = new ArrayList<>();
        if (alertsEnabled) {
            for (PantryItem item : allPantryItems) {
                if (ExpiryUtils.isExpiringSoon(item.getExpiryDate(), EXPIRING_SOON_DAYS)
                        || ExpiryUtils.isExpired(item.getExpiryDate())) {
                    expiringSoon.add(item);
                }
            }
        }

        View banner = findViewById(R.id.expiryBanner);
        TextView bannerText = findViewById(R.id.expiryBannerText);

        if (!alertsEnabled || expiringSoon.isEmpty()) {
            banner.setVisibility(View.GONE);
            showingExpiringOnly = false;
        } else {
            banner.setVisibility(View.VISIBLE);
            bannerText.setText(expiringSoon.size() + " ingredient(s) expiring soon - tap to "
                    + (showingExpiringOnly ? "show all" : "filter"));
        }

        List<PantryItem> listToShow = showingExpiringOnly ? expiringSoon : allPantryItems;
        recyclerView.setAdapter(new PantryAdapter(listToShow, this));
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