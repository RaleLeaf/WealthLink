package com.example.wealthlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class AccountView extends BaseActivity {
    TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setupDrawer(R.layout.activity_account_view);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        MaterialCardView contactInfoCard = findViewById(R.id.contact_info_card);

        userName  = findViewById(R.id.tvAccountName);
        userEmail = findViewById(R.id.tvAccountEmail);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String fname = documentSnapshot.getString("firstName");
                String mname = documentSnapshot.getString("middleName");
                String lname = documentSnapshot.getString("lastName");

                if (fname == null) fname = "";
                if (lname == null) lname = "";

                String fullName = fname + (mname != null ? " " + mname : "") + " " + lname;
                userName.setText(fullName.trim());

                String email = currentUser.getEmail();
                userEmail.setText(email.trim());

            } else {
                // Document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });


        contactInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAccount = new Intent(AccountView.this, UserAccountInfo.class);
                startActivity(viewAccount);
            }
        });

    }
}
