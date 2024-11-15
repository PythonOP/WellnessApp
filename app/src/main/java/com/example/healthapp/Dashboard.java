package com.example.healthapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.net.URI;

public class Dashboard extends AppCompatActivity {

    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out from Firebase Authentication
                FirebaseAuth.getInstance().signOut();

                // Redirect back to MainActivity
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear the back stack
                startActivity(intent);
                finish();  // Close the current activity
            }
        });
        CardView button1Card = findViewById(R.id.button1Card);
        CardView button2Card = findViewById(R.id.button2Card);
        CardView button3Card = findViewById(R.id.button3Card);
        CardView button4Card = findViewById(R.id.button4Card);

        // Set OnClickListener to the CardViews
        button1Card.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, MiniEatToolActivity.class);
            startActivity(intent);
        });
        button2Card.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, StepTracker.class);
            startActivity(intent);
        });
        button3Card.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, BMI.class);
            startActivity(intent);
        });
        button4Card.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ExpertVideos.class);
            startActivity(intent);
        });

        CardView hydrateNotifyBtn = findViewById(R.id.hydrateNotifyCard);
        hydrateNotifyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, WaterActivity.class);
            startActivity(intent);
        });

        // Initialize Firebase Auth and Firestore
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Initialize the welcomeText TextView
        TextView welcomeText = findViewById(R.id.welcomeText);
        // Get the current user ID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch the user data from Firestore
            firestore.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the user's name from the Firestore document
                                String name = document.getString("name");

                                // Update the welcomeText TextView with the user's name
                                welcomeText.setText("Welcome! " + name);
                            } else {
                                Toast.makeText(Dashboard.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Dashboard.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        Button connectBtn = findViewById(R.id.connectButton);
        connectBtn.setOnClickListener(v -> {
            //Usage of Telephony api
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+100));
            startActivity(intent);
        });
        Button viewAccountButton = findViewById(R.id.viewAccountButton);
        viewAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ViewAccountActivity.class);
            startActivity(intent);
        });
    }
}
