package com.example.userverifier;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        Button buttonReject = findViewById(R.id.buttonReject);

        // Initialize views
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textName = findViewById(R.id.textName);
        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewInstagramId = findViewById(R.id.textViewInstagramId);
        TextView textViewYoutubeChannelLink = findViewById(R.id.textViewYoutubeChannelLink);
        TextView textViewYoutubeChannelName = findViewById(R.id.textViewYoutubeChannelName);

        // Document Type
        TextView textViewDocumentType = findViewById(R.id.textViewDocumentType);
        // PDFView to display the PDF document
        PDFView pdfView = findViewById(R.id.pdfView);

        // Get data from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String profileImage = intent.getStringExtra("profileImage");
            String name = intent.getStringExtra("name");
            String name2 = intent.getStringExtra("name2");
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
            textViewName.setText(Html.fromHtml("<font color='#000000'>UserName: </font>" + (name != null ? "<font color='#808080'>" + name + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textName.setText(Html.fromHtml("<font color='#000000'>Name: </font>" + (name2 != null ? "<font color='#808080'>" + name2 + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewPhoneNumber.setText(Html.fromHtml("<font color='#000000'>Contact Number: </font>" + (phoneNumber != null ? "<font color='#808080'>" + phoneNumber + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewEmail.setText(Html.fromHtml("<font color='#000000'>Email: </font>" + (email != null ? "<font color='#808080'>" + email + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewInstagramId.setText(Html.fromHtml("<font color='#000000'>Instagram Id: </font>" + (instagramId != null ? "<font color='#808080'>" + instagramId + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewYoutubeChannelName.setText(Html.fromHtml("<font color='#000000'>Youtube Channel Name: </font>" + (youtubeChannelName != null ? "<font color='#808080'>" + youtubeChannelName + "</font>" : "<font color='#808080'>Not recorded</font>")));
            textViewYoutubeChannelLink.setText("Youtube Channel Link: " + (youtubeChannelLink != null ? youtubeChannelLink : "Not recorded"));

            // Set Document Type
            textViewDocumentType.setText(Html.fromHtml("<font color='#000000'>Document Type: </font>" + (documentType != null ? "<font color='#808080'>" + documentType + "</font>" : "<font color='#808080'>Not recorded</font>")));

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
            // Button click to Approve
            buttonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show a confirmation dialog for rejection
                    new AlertDialog.Builder(ViewApprovedActivity.this)
                            .setTitle("Cancel Request")
                            .setMessage("Are you sure you want to cancel the approved request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Get the user identifier from Intent
                                    String userId = intent.getStringExtra("userId");

                                    // Get the document URL from Intent
                                    String documentUrl = intent.getStringExtra("documentUrl");

                                    // Delete the PDF document from storage based on the URL
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(documentUrl);
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Deletion of PDF successful

                                            // Now, delete the "request_verification" node
                                            DatabaseReference userRef = databaseReference.child(userId);
                                            DatabaseReference requestVerificationRef = userRef.child("request_verification");
                                            requestVerificationRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Deletion of request_verification node successful

                                                    // Create the "verifyid" child with the value "1" under the "Creators" table
                                                    DatabaseReference creatorsRef = FirebaseDatabase.getInstance().getReference("Creators");
                                                    creatorsRef.child(userId).child("verifyid").setValue("3").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // "verifyid" created with the value "1" successfully
                                                            finish();
                                                            Toast.makeText(ViewApprovedActivity.this, "Verification Request Rejected!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(ViewApprovedActivity.this, "Error creating verifyid: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ViewApprovedActivity.this, "Error deleting request_verification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewApprovedActivity.this, "Error deleting PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            // Get the document URL from Intent
            String documentUrl = intent.getStringExtra("documentUrl");

            // Load and display the PDF document
            new DownloadPdfTask(pdfView).execute(documentUrl);
        }
    }

    private class DownloadPdfTask extends AsyncTask<String, Void, byte[]> {
        private PDFView pdfView;

        public DownloadPdfTask(PDFView pdfView) {
            this.pdfView = pdfView;
        }

        @Override
        protected byte[] doInBackground(String... params) {
            String pdfUrl = params[0];
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    return byteArrayOutputStream.toByteArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(byte[] result) {
            if (result != null) {
                pdfView.fromBytes(result)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .pageFitPolicy(FitPolicy.WIDTH)
                        .load();
            } else {
                Toast.makeText(ViewApprovedActivity.this, "Error loading PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
