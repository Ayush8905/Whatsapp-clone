package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityforworkingchats extends AppCompatActivity {
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView name;
    private ImageView call, videocall, send;
    private EditText message;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private String receivedData, receiverEmail, currentUserEmail;
    private String chatId,currentusername=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityforworkingchats);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        listView = findViewById(R.id.idforlistview);
        name = findViewById(R.id.name);
        call = findViewById(R.id.idforcall);
        videocall = findViewById(R.id.idforvideocall);
        message = findViewById(R.id.idforentermessage);
        send = findViewById(R.id.idforsendmessage);

        // Initialize ArrayList and Adapter for ListView
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setSelection(items.size()-1);
         listView.setAdapter(adapter);

        // Receiving data from intent
        receivedData = getIntent().getStringExtra("data_key");
        if (receivedData != null) {
            name.setText(receivedData);
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("user")
                .whereEqualTo("Name", receivedData)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            receiverEmail = document.getString("Email");
                           // currentusername = document.getString("Name");
                            if (receiverEmail != null) {
                                setupChat(currentUserEmail, receiverEmail);
                            } else {
                                Toast.makeText(this, "Receiver email not found", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch receiver email", Toast.LENGTH_SHORT).show();
                    }
                });

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
                        Toast.makeText(this, "Failed to fetch receiver email", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set click listeners
        call.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activityforqrcode.class)));
        videocall.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), activityforqrcode.class)));

        send.setOnClickListener(v -> {
            String messageText = message.getText().toString().trim();
            if (!messageText.isEmpty()) {
                addMessageToChat(chatId, currentusername+" : "+messageText);
                message.setText(""); // Clear the input field
            } else {
                Toast.makeText(activityforworkingchats.this, "Enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChat(String userEmail1, String userEmail2) {
        String chatId1 = userEmail1 + userEmail2;
        String chatId2 = userEmail2 + userEmail1;
        chatId = chatId1;

        DocumentReference chatRef1 = db.collection("chats").document(chatId1);
        DocumentReference chatRef2 = db.collection("chats").document(chatId2);

        chatRef1.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                chatId = chatId1;
                attachMessageListener(chatRef1);
            } else {
                chatRef2.get().addOnSuccessListener(documentSnapshot2 -> {
                    if (documentSnapshot2.exists()) {
                        chatId = chatId2;
                        attachMessageListener(chatRef2);
                    } else {
                        createChat(chatRef1);
                        attachMessageListener(chatRef1);
                    }
                }).addOnFailureListener(e -> Log.w("Firestore", "Error getting document", e));
            }
        }).addOnFailureListener(e -> Log.w("Firestore", "Error getting document", e));
    }

    private void createChat(DocumentReference chatRef) {
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("messages", new ArrayList<String>());
        chatRef.set(chatData).addOnSuccessListener(aVoid ->
                Toast.makeText(activityforworkingchats.this, "Chat created successfully", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(activityforworkingchats.this, "Failed to create chat", Toast.LENGTH_SHORT).show()
        );
    }

    private void attachMessageListener(@NonNull DocumentReference docRef) {
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<String> messages = (List<String>) documentSnapshot.get("messages");
                if (messages != null) {
                    items.clear();
                    items.addAll(messages);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addMessageToChat(String documentId, String messageText) {
        DocumentReference docRef = db.collection("chats").document(documentId);

        docRef.update("messages", FieldValue.arrayUnion(messageText))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activityforworkingchats.this, "Message sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating document", e);
                    Toast.makeText(activityforworkingchats.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }
}

