package com.example.healthapp;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ViewAccountActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "profile_update_channel";
    private static final int NOTIFICATION_ID = 1;


    private TextView welcomeText, emailText, nameText, phoneText;
    private ImageView profileImage;
    private Button changeButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        createNotificationChannel();

        welcomeText = findViewById(R.id.welcomeText);
        emailText = findViewById(R.id.emailText);
        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        profileImage = findViewById(R.id.profileImage); // Placeholder profile image
        changeButton = findViewById(R.id.changeButton);

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            loadUserProfile();
        }

        changeButton.setOnClickListener(v -> promptForUpdate());
    }

    private void loadUserProfile() {
        String userId = currentUser.getUid();
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("phone");

                    // Set the UI with user data
                    welcomeText.setText("Welcome, " + name + "!");
                    emailText.setText("Email: " + email);
                    nameText.setText("Name: " + name);
                    phoneText.setText("Phone: " + phone);
                }
            } else {
                Toast.makeText(ViewAccountActivity.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptForUpdate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20); // Optional: Padding for better UI

        final EditText inputName = new EditText(this);
        inputName.setHint("Enter new name");

        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("Enter new phone number");


        layout.addView(inputName);
        layout.addView(inputPhone);

        builder.setView(layout);

        // Set the positive button to update the data
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            String newPhone = inputPhone.getText().toString().trim();

            // Check if the fields are not empty before updating
            if (newName.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Fields can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                updateUserDetails(newName, newPhone);
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }


    private void updateUserDetails(String newName, String newPhone) {
        if (newName.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "Fields can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", newName);
        updatedData.put("phone", newPhone);

        firestore.collection("users").document(userId).update(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update UI after successful update
                nameText.setText("Name: " + newName);
                phoneText.setText("Phone: " + newPhone);
                welcomeText.setText("Welcome, " + newName + "!");
                Toast.makeText(ViewAccountActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

                // Show notification after successful update
                showNotification("Your profile is now updated.");
            } else {
                Toast.makeText(ViewAccountActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Create Notification Channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Profile Updates";
            String description = "Channel for profile update notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    // Method to show notification
    private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Profile Update")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
