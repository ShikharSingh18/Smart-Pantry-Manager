package com.shikharsingh.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiring_alerts_enabled";

    private ImageView iconNotification;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        iconNotification = findViewById(R.id.iconNotification);
        SwitchCompat switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        boolean alertsEnabled = prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
        switchExpiryAlerts.setChecked(alertsEnabled);
        updateNotificationIcon(alertsEnabled);

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            updateNotificationIcon(isChecked);
            Toast.makeText(this,
                    isChecked ? "Expiring soon alerts turned on" : "Expiring soon alerts turned off",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void updateNotificationIcon(boolean enabled) {
        iconNotification.setImageResource(
                enabled ? R.drawable.expiry_notifications_toggle_on : R.drawable.expiry_notifications_toggle_off);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}