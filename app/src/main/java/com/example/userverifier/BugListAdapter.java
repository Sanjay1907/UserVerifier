package com.example.userverifier;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class BugListAdapter extends ArrayAdapter<Bug> {
    private final Context context;
    private final ArrayList<Bug> bugList;

    public BugListAdapter(Context context) {
        super(context, R.layout.bug_list_item);
        this.context = context;
        this.bugList = new ArrayList<>();
    }

    public void addBug(Bug bug) {
        bugList.add(bug);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bugList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.bug_list_item, null, true);

        TextView userNameTextView = rowView.findViewById(R.id.userNameTextView);
        TextView idTextView = rowView.findViewById(R.id.idTextView);
        TextView descriptionTextView = rowView.findViewById(R.id.descriptionTextView);
        ImageView bugImageView = rowView.findViewById(R.id.bugImageView);

        Bug bug = bugList.get(position);
        userNameTextView.setText("User Name : " + bug.getUserName());
        idTextView.setText("Bug ID : " + bug.getId());
        descriptionTextView.setText("Bug Description : " + bug.getDescription());

        // Load bug image using Glide (you need to add Glide library to your project)
        Glide.with(BugListAdapter.this.getContext())
                .load(bug.getImageUrl())
                .placeholder(R.drawable.noimg)
                .error(R.drawable.noimg)
                .into(bugImageView);

        // Add click listener to the ImageView to show the image in a dialog
        bugImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(bug.getImageUrl());
            }
        });

        return rowView;
    }

    private void showImageDialog(String imageUrl) {
        Dialog imageDialog = new Dialog(context);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.image_dialog); // Create a custom layout for the dialog
        ImageView imageView = imageDialog.findViewById(R.id.imageViewDialog);
        Button closeButton = imageDialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss(); // Dismiss the dialog when the "Close" button is clicked
            }
        });
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        imageDialog.show();
    }
}
