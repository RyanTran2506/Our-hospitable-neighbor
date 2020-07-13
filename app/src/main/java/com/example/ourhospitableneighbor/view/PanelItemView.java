package com.example.ourhospitableneighbor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.model.Post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;

public class PanelItemView extends LinearLayout {
    private TextView txtTitle;
    private TextView txtAddress;
    private TextView txtDistance;
    private ImageView imgThumb;
    private ConstraintLayout constraintLayout;
    private Post post;

    private static DecimalFormat fmt = new DecimalFormat("###,###.##");
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
        constraintLayout = findViewById(R.id.PanelItemView_ConstraintLayout);

        setOrientation(LinearLayout.VERTICAL);
        setFocusable(true);
        setClickable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imgThumb.setClipToOutline(true);
        }

        // Set background to ?attr/selectableItemBackground
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        setBackgroundResource(outValue.resourceId);
    }

    public void setConstraintLayoutPadding(int left, int top, int right, int bottom) {
        constraintLayout.setPadding(left, top, right, bottom);

    }

    public void setPost(Post post) {
        this.post = post;
        txtTitle.setText(post.getPostTitle());
        txtAddress.setText(post.getAddress());

        setThumbnailImage();
        setDistanceText();
    }

    private void setThumbnailImage() {
        String thumbnail = post.getThumbnail();
        if (thumbnail != null) {
            Glide.with(getContext())
                    .load(storageReference.child(thumbnail))
                    .into(imgThumb);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setDistanceText() {
        Float distance = post.getDistanceFromUserLocation();
        if (distance == null) {
            txtDistance.setText(null);
        } else {
            int distanceRounded = Math.round(distance);
            if (distanceRounded >= 1000) {
                txtDistance.setText(fmt.format(distanceRounded / 1000f) + "km away");
            } else {
                txtDistance.setText(fmt.format(distanceRounded) + "m away");
            }
        }
    }
}
