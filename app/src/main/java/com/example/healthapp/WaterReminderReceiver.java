package com.example.healthapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

public class WaterReminderReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "WaterReminderPrefs";
    private static final String KEY_NOTIFICATION_COUNT = "notificationCount";
    private static final int MAX_NOTIFICATIONS = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int notificationCount = preferences.getInt(KEY_NOTIFICATION_COUNT, 0);

        if (notificationCount < MAX_NOTIFICATIONS) {
            // Show water reminder notification
            showWaterNotification(context);

            // Increment and save notification count
            notificationCount++;
            preferences.edit().putInt(KEY_NOTIFICATION_COUNT, notificationCount).apply();

            // Schedule the next reminder
            scheduleNextReminder(context);
        }
    }

    private void showWaterNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WATER_REMINDER")
                .setSmallIcon(R.drawable.ic_water) // Replace with your water icon
                .setContentTitle("Drink Water Reminder")
                .setContentText("Time to hydrate! Drink some water.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Create an intent to open the app when notification is tapped
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, builder.build());
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNextReminder(Context context) {
        Intent reminderIntent = new Intent(context, WaterReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long nextTriggerTime = System.currentTimeMillis() + (60 * 1000); // Set for the next 1 minute

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent);
    }
}
