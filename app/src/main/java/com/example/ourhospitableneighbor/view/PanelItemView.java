package com.example.ourhospitableneighbor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.model.Job;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;

public class PanelItemView extends LinearLayout {
    private TextView txtTitle;
    private TextView txtAddress;
    private TextView txtDistance;
    private ImageView imgThumb;
    private Job job;

    private static DecimalFormat fmt = new DecimalFormat();
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

    public PanelItemView(Context context) {
        super(context);
        init();
    }

    public PanelItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PanelItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Populate with views from the layout file
        inflate(getContext(), R.layout.panel_item, this);

        txtTitle = findViewById(R.id.txt_title);
        txtAddress = findViewById(R.id.txt_address);
        txtDistance = findViewById(R.id.txt_distance);
        imgThumb = findViewById(R.id.imageView);

        setOrientation(LinearLayout.VERTICAL);
        setFocusable(true);
        setClickable(true);

        // Set background to ?attr/selectableItemBackground
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
    }

    public void setJob(Job job) {
        this.job = job;
        txtTitle.setText(job.getJobTitle());
        txtAddress.setText(job.getAddress());

        setThumbnailImage();
        setDistanceText();
    }

    private void setThumbnailImage() {
        String thumbnail = job.getThumbnail();
        if (thumbnail != null) {
            Glide.with(getContext()).load(storageReference.child(thumbnail)).into(imgThumb);
        }

    }

    private void setDistanceText() {
        Float distance = job.getDistanceFromUserLocation();
        if (distance == null) {
            txtDistance.setText(null);
        } else {
            txtDistance.setText(fmt.format(Math.round(distance)) + "m away");
        }
    }
}
