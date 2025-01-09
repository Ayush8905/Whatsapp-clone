package com.example.whatsappclone;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class activityforstatusshow extends AppCompatActivity {
    private String username,myup;
    private FirebaseFirestore db;
    private TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activityforstatusshow);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            TextView textView = findViewById(R.id.idfortextshow);
            show = findViewById(R.id.status);
            textView.setText("Status");

            username = getIntent().getStringExtra("data_key");
            textView.setText(username);
            myup=getIntent().getStringExtra("data_key1");
            show.setText(myup);

            // Initialize Firestore
            db = FirebaseFirestore.getInstance();

            // Fetch and display status
            fetchStatus();

            return insets;
        });
    }

    private void fetchStatus() {
        db.collection("status")
                .whereEqualTo("Name", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String bio = document.getString("Bio");
                                show.setText(bio);
                                break;
                            }
                        } else {
                            Toast.makeText(this, "Status document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch status document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
