package com.example.healthapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class WaterActivity extends AppCompatActivity {

    private EditText intervalInput;
    private Button setNotificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        intervalInput = findViewById(R.id.intervalInput);
        setNotificationBtn = findViewById(R.id.setNotificationBtn);

        createNotificationChannel(); // Create notification channel

        setNotificationBtn.setOnClickListener(v -> {
            String intervalText = intervalInput.getText().toString();

            if (!intervalText.isEmpty()) {
                int interval = Integer.parseInt(intervalText);
                setWaterReminder(interval);
            } else {
                Toast.makeText(WaterActivity.this, "Please enter a valid interval!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setWaterReminder(int interval) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WaterReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + (interval * 60 * 500);
        long repeatInterval = interval * 60 * 500;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, repeatInterval, pendingIntent);
        Toast.makeText(this, "Water reminder set for every " + interval + " minutes", Toast.LENGTH_SHORT).show();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WaterReminderChannel";
            String description = "Channel for Water Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("WATER_REMINDER", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
