package com.example.whatsappclone;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

public class fragmentforchats extends Fragment {

    private RecyclerViewAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserEmail;
    private String currentusername;
    FirebaseFirestore firestore;
    String prophoto;

    public fragmentforchats() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentforchats, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.idforrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                            fetchUsers(recyclerView);
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No current user found", Toast.LENGTH_SHORT).show();

        }

        return view;
    }

    private void fetchUsers(RecyclerView recyclerView) {

        ArrayList<Item> itemList = new ArrayList<>();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("Name");
                                prophoto = document.getString("profilePhotoUrl");
                                String phone = "Busy";  // Assuming a static phone number for demonstration


                                if (!name.equals(currentusername)) {
                                    String bio = document.contains("Bio") ? document.getString("Bio") : phone;
                                    itemList.add(new Item(R.drawable.imageforcommunites, name, bio));
                                }
                            }
                            adapter = new RecyclerViewAdapter(itemList, true);

                            // adapter = new RecyclerViewAdapter(itemList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}