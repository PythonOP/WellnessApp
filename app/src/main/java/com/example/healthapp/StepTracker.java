    package com.example.healthapp;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.hardware.Sensor;
    import android.hardware.SensorEvent;
    import android.hardware.SensorEventListener;
    import android.hardware.SensorManager;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    public class StepTracker extends AppCompatActivity implements SensorEventListener {

        private SensorManager sensorManager;
        private Sensor stepCounterSensor;
        private TextView stepCountText;
        private Button resetButton;
        private boolean isCounterSensorAvailable;
        private int initialStepCount = 0;
        private int stepsSinceReset = 0;
        private boolean resetPressed = false;

        // SharedPreferences constants
        private static final String PREFS_NAME = "StepTrackerPrefs";
        private static final String KEY_INITIAL_STEP_COUNT = "initialStepCount";
        private static final String KEY_STEPS_SINCE_RESET = "stepsSinceReset";
        private static final String KEY_RESET_PRESSED = "resetPressed";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_step_tracker);

            // Initialize UI elements
            stepCountText = findViewById(R.id.stepCountText);
            ImageView stepIcon = findViewById(R.id.stepIcon);
            resetButton = findViewById(R.id.resetButton);
            CardView stepCard = findViewById(R.id.stepCard);

            // Initialize Sensor Manager
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }

            // Get the step counter sensor
            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
                stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                isCounterSensorAvailable = true;
            } else {
                isCounterSensorAvailable = false;
                Toast.makeText(this, "Step Counter Sensor is not available!", Toast.LENGTH_SHORT).show();
            }

            // Load saved data from SharedPreferences
            loadSavedData();

            // Set Reset Button OnClickListener
            resetButton.setOnClickListener(v -> {
                Log.d("StepTracker", "Reset button pressed");
                resetPressed = true;
                initialStepCount = stepsSinceReset; // Record the step count at reset
                stepsSinceReset = 0; // Reset the internal step count
                stepCountText.setText("Steps: " + stepsSinceReset);
            });
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                Log.d("StepTracker", "Total steps from sensor: " + sensorEvent.values[0]);

                int totalSteps = (int) sensorEvent.values[0];

                if (resetPressed) {
                    stepsSinceReset = totalSteps - initialStepCount;
                } else {
                    stepsSinceReset = totalSteps;
                }

                Log.d("StepTracker", "Steps since reset: " + stepsSinceReset);
                stepCountText.setText("Steps: " + stepsSinceReset);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not needed for this implementation
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (isCounterSensorAvailable) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            if (isCounterSensorAvailable) {
                sensorManager.unregisterListener(this);
            }

            // Save current step data in SharedPreferences
            saveStepData();
        }

        // Method to save step data in SharedPreferences
        private void saveStepData() {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_INITIAL_STEP_COUNT, initialStepCount);
            editor.putInt(KEY_STEPS_SINCE_RESET, stepsSinceReset);
            editor.putBoolean(KEY_RESET_PRESSED, resetPressed);
            editor.apply();
        }

        // Method to load saved step data from SharedPreferences
        private void loadSavedData() {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            initialStepCount = sharedPreferences.getInt(KEY_INITIAL_STEP_COUNT, 0);
            stepsSinceReset = sharedPreferences.getInt(KEY_STEPS_SINCE_RESET, 0);
            resetPressed = sharedPreferences.getBoolean(KEY_RESET_PRESSED, false);

            stepCountText.setText("Steps: " + stepsSinceReset);
        }
    }
