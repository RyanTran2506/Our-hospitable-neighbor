package com.example.ourhospitableneighbor.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ourhospitableneighbor.R;
import com.example.ourhospitableneighbor.helper.SizeConverter;
import com.example.ourhospitableneighbor.model.Post;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class PostInfoWindowView extends RelativeLayout {
    private Post post;
    private TextView txtTitle;
    private TextView txtAddress;
    private ImageView imgThumb;
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
    private Marker marker;


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

        int padding = SizeConverter.fromDpToPx(getResources(), 3);
        setPadding(0, padding, 0, padding);
    }

    public void setPost(Post post) {
        this.post = post;
        txtTitle.setText(post.getPostTitle());
        txtAddress.setText(post.getAddress());

        setThumbnailImage();
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    private void setThumbnailImage() {
        String thumbnail = post.getThumbnail();
        if (thumbnail != null) {
            Glide.with(getContext())
                    .load(storageReference.child(thumbnail))
                    .placeholder(R.drawable.placeholder_square)
                    .dontAnimate()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (dataSource != DataSource.MEMORY_CACHE && marker != null) {
                                marker.showInfoWindow();
                            }
                            return false;
                        }
                    })
                    .into(imgThumb);
        }

    }

}
