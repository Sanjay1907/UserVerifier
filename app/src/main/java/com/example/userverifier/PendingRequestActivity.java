package com.example.userverifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PendingRequestActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingrequest);

        tableLayout = findViewById(R.id.tableLayout);

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Creators");

        // Read data from the Firebase Realtime Database and filter users with "request_verification" child
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing rows from the table
                tableLayout.removeAllViews();

                // Inside the loop where you add rows to the table
                for (DataSnapshot creatorSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot requestVerificationSnapshot = creatorSnapshot.child("request_verification");

                    if (requestVerificationSnapshot.exists()) {
                        for (DataSnapshot userVerificationData : requestVerificationSnapshot.getChildren()) {
                            String verificationStatus = userVerificationData.child("verification").getValue(String.class);

                            if ("0".equals(verificationStatus)) { // Check if verification is "0"
                                String name = creatorSnapshot.child("name").getValue(String.class);
                                String phoneNumber = creatorSnapshot.child("phoneNumber").getValue(String.class);
                                if (name != null && phoneNumber != null) {
                                    // Create a new TableRow
                                    TableRow tableRow = (TableRow) LayoutInflater.from(PendingRequestActivity.this).inflate(R.layout.table_row, null);

                                    // Find the TextViews and Button within the TableRow
                                    TextView textViewName = tableRow.findViewById(R.id.textViewName);
                                    Button btnViewDetails = tableRow.findViewById(R.id.btnViewDetails);

                                    // Set the values for the TextViews
                                    textViewName.setText(name);

                                    // Set onClickListener to start ViewDetailsActivity with all details
                                    btnViewDetails.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Create an intent to launch ViewDetailsActivity
                                            Intent intent = new Intent(PendingRequestActivity.this, ViewDetailsActivity.class);

                                            // Pass all details as extras
                                            String email = creatorSnapshot.child("email").getValue(String.class);
                                            String profileImage = creatorSnapshot.child("profileImage").getValue(String.class);
                                            String instagramId = creatorSnapshot.child("Instagram id").getValue(String.class);
                                            String youtubeChannelLink = creatorSnapshot.child("Youtube Channel Link").getValue(String.class);
                                            String youtubeChannelName = creatorSnapshot.child("Youtube Channel name").getValue(String.class);

                                            intent.putExtra("userId", creatorSnapshot.getKey());
                                            intent.putExtra("name", name);
                                            intent.putExtra("phoneNumber", phoneNumber);
                                            intent.putExtra("email", email);
                                            intent.putExtra("profileImage", profileImage);
                                            intent.putExtra("instagramId", instagramId);
                                            intent.putExtra("youtubeChannelLink", youtubeChannelLink);
                                            intent.putExtra("youtubeChannelName", youtubeChannelName);
                                            intent.putExtra("documentType", userVerificationData.child("documentType").getValue(String.class)); // Pass document type
                                            intent.putExtra("documentUrl", userVerificationData.child("documentUrl").getValue(String.class)); // Pass document URL

                                            startActivity(intent);
                                        }
                                    });

                                    tableLayout.addView(tableRow);
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
            }
        });
    }
}
