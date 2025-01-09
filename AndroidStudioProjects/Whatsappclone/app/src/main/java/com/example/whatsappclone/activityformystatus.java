package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class activityformystatus extends AppCompatActivity {

    private EditText data;
    private Button send, show;
    private String currentUserEmail, currentusername;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activityformystatus);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = findViewById(R.id.idformess);
        send = findViewById(R.id.idforsendstatus);
        show = findViewById(R.id.idforshow);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
            db.collection("user")
                    .whereEqualTo("Email", currentUserEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                currentusername = document.getString("Name");
                                break;
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No current user found", Toast.LENGTH_SHORT).show();
        }

        send.setOnClickListener(v -> {
            String statusText = data.getText().toString();
            if (!statusText.isEmpty()) {
                updateStatus(currentusername, statusText);
            } else {
                Toast.makeText(this, "Status cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        show.setOnClickListener(v -> showCurrentStatus(currentusername));
    }

    private void updateStatus(String username, String statusText) {
        DocumentReference statusDocRef = db.collection("status").document(username);
        statusDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Document exists, update the status
                    statusDocRef.update("Bio", statusText)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());

                    statusDocRef.update("sta", true)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Status flag updated", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status flag", Toast.LENGTH_SHORT).show());
                } else {
                    // Document does not exist, create it
                    Map<String, Object> statusData = new HashMap<>();
                    statusData.put("Name", username);
                    statusData.put("Bio", statusText);
                    statusData.put("sta", true);
                    statusDocRef.set(statusData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Status document created and updated", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to create status document", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Failed to fetch status document", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCurrentStatus(String username) {
        DocumentReference statusDocRef = db.collection("status").document(username);
        statusDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String bio = document.getString("Bio");
                    Intent i =new Intent(this,activityforstatusshow.class);
                    i.putExtra("data_key1",bio);
                    startActivity(i);
                    Toast.makeText(this, "Current status: " + bio, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Status document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to fetch status document", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
