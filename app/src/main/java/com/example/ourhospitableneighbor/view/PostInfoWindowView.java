package com.example.ourhospitableneighbor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.model.Post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostInfoWindowView extends RelativeLayout {
    private Post post;
    private TextView txtTitle;
    private TextView txtAddress;
    private ImageView imgThumb;
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");


    public PostInfoWindowView(Context context) {
        super(context);
        init();
    }

    public PostInfoWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PostInfoWindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.post_info_window_view, this);
        txtTitle = findViewById(R.id.PostInfoWindowView_Title);
        txtAddress = findViewById(R.id.PostInfoWindowView_Address);
        imgThumb = findViewById(R.id.PostInfoWindowView_Image);

        // Convert dp to px
        int padding = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                3,
                getResources().getDisplayMetrics())
        );
        setPadding(0,padding,0,padding);
    }

    public void setPost(Post post) {
        this.post = post;
        txtTitle.setText(post.getPostTitle());
        txtAddress.setText(post.getAddress());

        setThumbnailImage();
    }

    private void setThumbnailImage() {
        String thumbnail = post.getThumbnail();
        if (thumbnail != null) {
            Glide.with(getContext()).load(storageReference.child(thumbnail)).into(imgThumb);
        }

    }

}
