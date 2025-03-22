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
    private static final String KEY_ACCENT_LIGHT = "accent_light";
    private static final String KEY_ACCENT_DARK = "accent_dark";

    // Default accent color (teal)
    private static final int DEFAULT_ACCENT = Color.parseColor("#03DAC5");
    private static final int DEFAULT_ACCENT_LIGHT = Color.parseColor("#4DEFE0");
    private static final int DEFAULT_ACCENT_DARK = Color.parseColor("#00B5A3");

    private int selectedColor;
    private int accentLight;
    private int accentDark;
    private View colorPreview;
    private View lightVariantPreview;
    private View darkVariantPreview;
    private boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if dark mode is enabled
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        // Set theme based on system preference
        if (isDarkMode) {
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
        
        // Add views for light and dark variant previews
        lightVariantPreview = findViewById(R.id.lightVariantPreview);
        darkVariantPreview = findViewById(R.id.darkVariantPreview);

        // Load saved color or use default
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedColor = prefs.getInt(KEY_ACCENT_COLOR, DEFAULT_ACCENT);
        updateVariants(selectedColor);

        // Preset colors - adjust based on theme
        int[] colors;
        if (isDarkMode) {
            colors = new int[] {
                Color.parseColor("#03DAC5"), // Default teal
                Color.parseColor("#CF3367"), // Darker Pink
                Color.parseColor("#3D50B5"), // Darker Blue
                Color.parseColor("#E59100"), // Darker Orange
                Color.parseColor("#388E3C"), // Darker Green
                Color.parseColor("#7B1FA2")  // Darker Purple
            };
        } else {
            colors = new int[] {
                Color.parseColor("#03DAC5"), // Default
                Color.parseColor("#FF4081"), // Pink
                Color.parseColor("#536DFE"), // Blue
                Color.parseColor("#FFAB40"), // Orange
                Color.parseColor("#4CAF50"), // Green
                Color.parseColor("#9C27B0")  // Purple
            };
        }

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
        
        // Initialize with selected/default color
        colorPreview.setBackgroundColor(selectedColor);
    }

    private void selectColor(int color) {
        selectedColor = color;
        colorPreview.setBackgroundColor(color);
        updateVariants(color);
    }

    private void updateVariants(int color) {
        // Calculate lighter and darker variants
        accentLight = getLighterVariant(color);
        accentDark = getDarkerVariant(color);
        
        // Update preview
        if (lightVariantPreview != null) {
            lightVariantPreview.setBackgroundColor(accentLight);
        }
        if (darkVariantPreview != null) {
            darkVariantPreview.setBackgroundColor(accentDark);
        }
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
        prefs.edit()
            .putInt(KEY_ACCENT_COLOR, selectedColor)
            .putInt(KEY_ACCENT_LIGHT, accentLight)
            .putInt(KEY_ACCENT_DARK, accentDark)
            .apply();

        // Redirect to MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    
    /**
     * Generate a lighter variant of a color
     */
    public static int getLighterVariant(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        
        // Increase brightness while preserving hue and saturation
        hsv[2] = Math.min(hsv[2] + 0.2f, 1.0f);
        
        return Color.HSVToColor(hsv);
    }
    
    /**
     * Generate a darker variant of a color
     */
    public static int getDarkerVariant(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        
        // Slightly increase saturation for richer color
        hsv[1] = Math.min(hsv[1] + 0.1f, 1.0f);
        
        // Decrease brightness
        hsv[2] = Math.max(hsv[2] - 0.2f, 0.0f);
        
        return Color.HSVToColor(hsv);
    }
    
    /**
     * Calculate readable text color (white or black) based on background color
     */
    public static int getReadableTextColor(int backgroundColor) {
        // Calculate luminance to determine if text should be white or black
        double luminance = (0.299 * Color.red(backgroundColor) + 
                            0.587 * Color.green(backgroundColor) + 
                            0.114 * Color.blue(backgroundColor)) / 255;
                            
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
    
    /**
     * Get the accent color from SharedPreferences
     */
    public static int getAccentColor(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_ACCENT_COLOR, DEFAULT_ACCENT);
    }
    
    /**
     * Get the light variant of the accent color
     */
    public static int getAccentLight(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_ACCENT_LIGHT, DEFAULT_ACCENT_LIGHT);
    }
    
    /**
     * Get the dark variant of the accent color
     */
    public static int getAccentDark(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_ACCENT_DARK, DEFAULT_ACCENT_DARK);
    }
}