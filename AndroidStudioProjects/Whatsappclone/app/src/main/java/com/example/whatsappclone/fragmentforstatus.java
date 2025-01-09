package com.example.whatsappclone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragmentforstatus extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerViewAdapter adapter;
    private String currentUserEmail;
    private String currentusername;
    private LinearLayout mystatus;

    public fragmentforstatus() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentforstatus, container, false);

        recyclerView = view.findViewById(R.id.idforrecycleview);
        mystatus = view.findViewById(R.id.layoutid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mystatus.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), activityformystatus.class);
            startActivity(i);
        });

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
                            fetchUsersAndCreateStatus();
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No current user found", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchUsersAndCreateStatus() {
        ArrayList<Item> itemList = new ArrayList<>();
        db.collection("user")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("Name");
                            String bio = document.contains("Bio") ? document.getString("Bio") : "";
                            if (!name.equals(currentusername)) {
                                checkAndCreateStatusDocument(document.getId(), name, bio, itemList);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndCreateStatusDocument(String userId, String name, String bio, ArrayList<Item> itemList) {
        DocumentReference statusDocRef = db.collection("status").document(userId);
        statusDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    if (Boolean.TRUE.equals(document.getBoolean("sta"))) {
                        itemList.add(new Item(R.drawable.imageforcommunites, name, bio));
                    }
                } else {
                    Map<String, Object> statusData = new HashMap<>();
                    statusData.put("Name", name);
                    statusData.put("Bio", bio);
                    statusData.put("sta", true);
                    statusDocRef.set(statusData)
                            .addOnSuccessListener(aVoid -> {
                                itemList.add(new Item(R.drawable.imageforcommunites, name, bio));
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to create status document for " + name, Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(getActivity(), "Failed to check status document existence", Toast.LENGTH_SHORT).show();
            }
            // Update the adapter after each document is processed
            adapter = new RecyclerViewAdapter(itemList, false);
            recyclerView.setAdapter(adapter);
        });
    }
}
