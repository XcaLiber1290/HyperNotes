package com.hypertron.hypernotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog; // External color picker library

public class AccentColorActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "HyperNotesPrefs";
    private static final String KEY_ACCENT_COLOR = "accent_color";

    private int selectedColor;
    private View colorPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme based on system preference
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.Theme_HyperNotes); // Uses Dark Mode theme
        } else {
            setTheme(R.style.Theme_HyperNotes); // Uses Light Mode theme
        }

        setContentView(R.layout.activity_accent_color);

        // Find UI elements
        colorPreview = findViewById(R.id.colorPreview);
        Button btnCustomColor = findViewById(R.id.btnCustomColor);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        LinearLayout colorOptions = findViewById(R.id.colorOptions);

        // Preset colors
        int[] colors = {
            Color.parseColor("#03DAC5"), // Default
            Color.parseColor("#FF4081"), // Pink
            Color.parseColor("#536DFE"), // Blue
            Color.parseColor("#FFAB40"), // Orange
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#9C27B0")  // Purple
        };

        for (int color : colors) {
            View colorView = new View(this);
            colorView.setBackgroundColor(color);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(16, 16, 16, 16);
            colorView.setLayoutParams(params);
            colorView.setOnClickListener(v -> selectColor(color));
            colorOptions.addView(colorView);
        }

        // Custom Color Picker
        btnCustomColor.setOnClickListener(v -> openColorPicker());

        // Confirm Button
        btnConfirm.setOnClickListener(v -> saveAndProceed());
    }

    private void selectColor(int color) {
        selectedColor = color;
        colorPreview.setBackgroundColor(color);
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, selectedColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                selectColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Do nothing if canceled
            }
        });
        colorPicker.show();
    }

    private void saveAndProceed() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_ACCENT_COLOR, selectedColor).apply();

        // Redirect to MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
