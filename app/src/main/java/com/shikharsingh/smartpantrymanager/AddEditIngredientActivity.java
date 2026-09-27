package com.shikharsingh.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

/**
 * Handles BOTH adding a new pantry item and editing an existing one.
 * If "pantry_id" is passed via Intent extra, it's edit mode; otherwise
 * it's add mode. Includes basic input validation.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private static final long NO_ID = -1L;

    private DatabaseHelper dbHelper;
    private EditText editName, editQuantity, editUnit, editExpiry;
    private long editingItemId = NO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbarAddEdit); // use your layout's actual toolbar id
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        editName = findViewById(R.id.editTextName);
        editQuantity = findViewById(R.id.editTextQuantity);
        editUnit = findViewById(R.id.editTextUnit);
        editExpiry = findViewById(R.id.editTextExpiry);
        Button buttonSave = findViewById(R.id.buttonSave);

        editingItemId = getIntent().getLongExtra("pantry_id", NO_ID);
        if (editingItemId != NO_ID) {
            loadExistingItem(editingItemId);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Ingredient");
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add Ingredient");
        }

        buttonSave.setOnClickListener(v -> saveItem());
    }

    private void loadExistingItem(long id) {
        List<PantryItem> allItems = dbHelper.getAllPantryItems();
        for (PantryItem item : allItems) {
            if (item.getId() == id) {
                editName.setText(item.getName());
                editQuantity.setText(String.valueOf(item.getQuantity()));
                editUnit.setText(item.getUnit());
                editExpiry.setText(item.getExpiryDate());
                break;
            }
        }
    }

    private void saveItem() {
        String name = editName.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Ingredient name is required");
            return;
        }

        double quantity;
        try {
            quantity = quantityStr.isEmpty() ? 0 : Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            editQuantity.setError("Enter a valid number");
            return;
        }

        if (editingItemId == NO_ID) {
            dbHelper.addPantryItem(name, quantity, unit, expiry);
        } else {
            dbHelper.updatePantryItem(editingItemId, name, quantity, unit, expiry);
        }
        Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
        finish(); // returns to MainActivity, which refreshes in onResume
    }
}