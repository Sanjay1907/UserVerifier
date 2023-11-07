package com.example.userverifier;

import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.ProgressDialog;

public class BugListActivity extends AppCompatActivity {
    private ListView bugListView;
    private BugListAdapter bugListAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_list);

        // Initialize Firebase Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        bugListView = findViewById(R.id.bugListView);
        bugListAdapter = new BugListAdapter(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching reported bugs...");
        progressDialog.show();

        // Attach a listener to retrieve bug reports
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot reportBugSnapshot = userSnapshot.child("report_bug");
                    for (DataSnapshot bugSnapshot : reportBugSnapshot.getChildren()) {
                        String userName = userSnapshot.child("name").getValue(String.class);
                        String id = bugSnapshot.child("id").getValue(String.class);
                        String description = bugSnapshot.child("description").getValue(String.class);
                        String imageUrl = bugSnapshot.child("imageUrl").getValue(String.class);

                        Bug bug = new Bug(userName, id, description, imageUrl);
                        bugListAdapter.addBug(bug);
                    }

                    // Set the adapter for the ListView
                    bugListView.setAdapter(bugListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
