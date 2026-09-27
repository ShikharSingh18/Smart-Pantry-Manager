package com.shikharsingh.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//Simple settings screen. Persists a toggle for "expiring-soon alerts" using SharedPreferences.

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "app_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiring_alerts_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Switch switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        boolean alertsEnabled = prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
        switchExpiryAlerts.setChecked(alertsEnabled);

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            Toast.makeText(this,
                    isChecked ? "Expiring-soon alerts enabled" : "Expiring-soon alerts disabled",
                    Toast.LENGTH_SHORT).show();
        });
    }
}