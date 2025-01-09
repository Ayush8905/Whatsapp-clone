package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splashscreen);
        double vq = 4000;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            boolean isLoggedIn = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getBoolean("isLoggedIn", false);


            Intent intent;
            if (isLoggedIn) {
                // User is logged in, navigate to HomeActivity
                intent = new Intent(this, MainActivity.class);
            } else {
                // User is not logged in, navigate to LoginActivity
                intent = new Intent(this, loginactivity.class);
            }
            startActivity(intent);
            finish();
            return insets;
        });
    }
}