package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    String receivedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.idfortoolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        // Receiving data from login activity email
        receivedData = getIntent().getStringExtra("Email");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavibar);

        // Load default fragment
        if (savedInstanceState == null) {
            Fragment fragment = new fragmentforchats();
//            Bundle bundle = new Bundle();
//            bundle.putString("Email", receivedData);
//            fragment.setArguments(bundle);
            addFragment(fragment);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            bundle.putString("Email", receivedData);

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_chsts) {
                selectedFragment = new fragmentforchats();
                selectedFragment.setArguments(bundle);
            } else if (itemId == R.id.navigation_status) {
                selectedFragment = new fragmentforstatus();
            } else if (itemId == R.id.navigation_communities) {
                selectedFragment = new fragmentforcommunities();
            } else if (itemId == R.id.navigation_calls) {
                selectedFragment = new fragmentforcalls();
            }

            if (selectedFragment != null) {
                addFragment(selectedFragment);
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_chats, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent i = new Intent(getApplicationContext(), activityforqrcode.class);

        if (itemId == R.id.action_qrcode) {
            startActivity(i);
            Toast.makeText(this, "Click On QR code", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_camera) {
            startActivity(i);
            Toast.makeText(this, "Click On Camera", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_new_group) {
            startActivity(i);
            Toast.makeText(this, "Click On New Group", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_new_broadcast) {
            startActivity(i);
            Toast.makeText(this, "Click On New Broadcast", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_linked_devices) {
            startActivity(i);
            Toast.makeText(this, "Click On Linked Devices", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_starred_messages) {
            startActivity(i);
            Toast.makeText(this, "Click On Starred Messages", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_payments) {
            startActivity(i);
            Toast.makeText(this, "Click On Payments", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            mAuth.signOut();
            getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isLoggedIn", false)
                    .apply();
            Intent intent = new Intent(this, loginactivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void addFragment(Fragment fragment) { // method for adding fragment in bottom navigation bar
        FragmentManager fm1 = getSupportFragmentManager();
        FragmentTransaction ft = fm1.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static class item {
    }
}
