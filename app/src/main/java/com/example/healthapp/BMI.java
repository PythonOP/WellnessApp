package com.example.healthapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BMI extends AppCompatActivity {
    EditText heightInput, weightInput;
    Button calculateBtn;
    TextView resultText;

    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "bmiPrefs";
    private static final String LAST_BMI = "last_bmi";
    private static final String LAST_BMI_CATEGORY = "last_bmi_category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        calculateBtn = findViewById(R.id.calculateBtn);
        resultText = findViewById(R.id.resultText);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load last BMI result if available
        loadLastBmi();

        calculateBtn.setOnClickListener(v -> {
            String heightStr = heightInput.getText().toString();
            String weightStr = weightInput.getText().toString();

            if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
                double heightInCm = Double.parseDouble(heightStr);
                double weight = Double.parseDouble(weightStr);

                // Convert height from centimeters to meters
                double height = heightInCm / 100;
                double bmi = weight / (height * height);
                String bmiCategory = getBmiCategory(bmi);

                resultText.setText("Your BMI is: " + bmi + "\nCategory: " + bmiCategory);

                // Save BMI result in SharedPreferences
                saveBmi(bmi, bmiCategory);
            } else {
                Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLastBmi() {
        // Get last stored BMI and category from SharedPreferences
        float lastBmi = sharedPreferences.getFloat(LAST_BMI, -1);
        String lastCategory = sharedPreferences.getString(LAST_BMI_CATEGORY, "");

        if (lastBmi != -1) {
            // Display the last BMI if available
            resultText.setText("Last BMI: " + lastBmi + "\nCategory: " + lastCategory);
        }
    }

    private void saveBmi(double bmi, String bmiCategory) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LAST_BMI, (float) bmi);
        editor.putString(LAST_BMI_CATEGORY, bmiCategory);
        editor.apply(); // Apply changes asynchronously

        Toast.makeText(this, "BMI saved!", Toast.LENGTH_SHORT).show();
    }

    private String getBmiCategory(double bmi) {
        // Show BMI using Toast for quick feedback
        Toast.makeText(this, "BMI: " + bmi, Toast.LENGTH_LONG).show();

        if (bmi < 18.5) return "Underweight";
        else if (bmi < 24.9) return "Normal";
        else if (bmi < 29.9) return "Overweight";
        else return "Obese";
    }
}
