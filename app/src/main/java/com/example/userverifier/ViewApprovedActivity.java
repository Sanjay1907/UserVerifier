package com.example.userverifier;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewApprovedActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Dialog imageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_approved);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Creators");
        imageDialog = new Dialog(this);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.image_dialog);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final ImageView imageViewDialog = imageDialog.findViewById(R.id.imageViewDialog);
        Button closeButton = imageDialog.findViewById(R.id.closeButton);
        // Initialize views
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewInstagramId = findViewById(R.id.textViewInstagramId);
        TextView textViewYoutubeChannelLink = findViewById(R.id.textViewYoutubeChannelLink);
        TextView textViewYoutubeChannelName = findViewById(R.id.textViewYoutubeChannelName);

        // Document Type and Button
        TextView textViewDocumentType = findViewById(R.id.textViewDocumentType);
        Button buttonViewDocument = findViewById(R.id.buttonViewDocument);
        Button buttonReject = findViewById(R.id.buttonReject);

        // Get data from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String profileImage = intent.getStringExtra("profileImage");
            String name = intent.getStringExtra("name");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String email = intent.getStringExtra("email");
            String instagramId = intent.getStringExtra("instagramId");
            String youtubeChannelLink = intent.getStringExtra("youtubeChannelLink");
            String youtubeChannelName = intent.getStringExtra("youtubeChannelName");

            // Document Type
            String documentType = intent.getStringExtra("documentType");

            // Load profile image using Glide
            Glide.with(this)
                    .load(profileImage)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(imageViewProfile);

            // Set text values with labels
            textViewName.setText(Html.fromHtml("<font color='#000000'>Name: </font>" + (name != null ? "<font color='#808080'>" + name + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewPhoneNumber.setText(Html.fromHtml("<font color='#000000'>Contact Number: </font>" + (phoneNumber != null ? "<font color='#808080'>" + phoneNumber + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewEmail.setText(Html.fromHtml("<font color='#000000'>Email: </font>" + (email != null ? "<font color='#808080'>" + email + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewInstagramId.setText(Html.fromHtml("<font color='#000000'>Instagram Id: </font>" + (instagramId != null ? "<font color='#808080'>" + instagramId + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewYoutubeChannelName.setText(Html.fromHtml("<font color='#000000'>Youtube Channel Name: </font>" + (youtubeChannelName != null ? "<font color='#808080'>" + youtubeChannelName + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewYoutubeChannelLink.setText("Youtube Channel Link: " + (youtubeChannelLink != null ? youtubeChannelLink : "Not recorded"));


            // Set Document Type
            textViewDocumentType.setText("Document Type: " + (documentType != null ? documentType : "Not recorded"));

            // Set an OnClickListener on the profile image to show the larger image in a dialog
            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.with(ViewApprovedActivity.this)
                            .load(profileImage)
                            .placeholder(R.drawable.default_profile_image)
                            .error(R.drawable.default_profile_image)
                            .into(imageViewDialog);
                    imageDialog.show();
                }
            });
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageDialog.dismiss(); // Dismiss the dialog when the "Close" button is clicked
                }
            });

            // Button click to view PDF document
            buttonViewDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the document URL from Intent
                    String documentUrl = intent.getStringExtra("documentUrl");

                    // Open the document using an Intent
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(Uri.parse(documentUrl), "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(pdfIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Button click to Approve
            buttonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show a confirmation dialog for approval
                    new AlertDialog.Builder(ViewApprovedActivity.this)
                            .setTitle("Cancel Request")
                            .setMessage("Are you sure you want to cancel the approved request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Get the user identifier from Intent
                                    String userId = intent.getStringExtra("userId");

                                    // Update verification status to "1" in the Firebase Realtime Database for the specific user
                                    databaseReference
                                            .child(userId) // Use the user identifier
                                            .child("request_verification")
                                            .child(userId)
                                            .child("verification")
                                            .setValue("0");

                                    // Finish the activity
                                    finish();
                                    Toast.makeText(ViewApprovedActivity.this, "Verification Request Cancelled!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }
    }
}
