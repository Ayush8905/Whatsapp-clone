package com.example.whatsappclone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class sinupactivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText signupEmailEditText, signupPasswordEditText, username, bio;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView profilephoto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinupactivty);

        signupEmailEditText = findViewById(R.id.signupEmailEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        username = findViewById(R.id.username1);
        bio = findViewById(R.id.bio);
        profilephoto = findViewById(R.id.idforprofilephoto);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createAccountButton.setOnClickListener(v -> {
            if (validateInput()) {
                createAccount();
            }
        });

        profilephoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilephoto.setImageURI(imageUri);
        }
    }

    private void createAccount() {
        String email = signupEmailEditText.getText().toString();
        String password = signupPasswordEditText.getText().toString();
        String name = username.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            uploadImage(user.getUid());
                        } else {
                            Toast.makeText(this, "Account Creation Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImage(String userId) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("profile_photos/" + userId + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveUserToFirestore(userId, uri.toString());
                        Toast.makeText(sinupactivity.this, "Profile photo uploaded", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(sinupactivity.this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveUserToFirestore(String userId, String profilePhotoUrl) {
        String email = signupEmailEditText.getText().toString();
        String name = username.getText().toString();
        String bioText = bio.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("Name", name);
        user.put("Bio", bioText);
        user.put("profilePhotoUrl", profilePhotoUrl);

        db.collection("user").document(userId).set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(sinupactivity.this, "User added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(sinupactivity.this, "Failed to add user to Firestore", Toast.LENGTH_SHORT).show());
    }

    private boolean validateInput() {
        String email = signupEmailEditText.getText().toString();
        String password = signupPasswordEditText.getText().toString();
        String name = username.getText().toString();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
