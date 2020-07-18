package com.example.ourhospitableneighbor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private final int MAX_IMAGE_ON_GALLERY = 6;
    private final Context context;
    private final ArrayList<Uri> imgs;
    private boolean isImageFitToScreen;

    public ImageAdapter(Context context, ArrayList<Uri> imgs){
        this.context = context;
        this.imgs = imgs;
    }
    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.image_gallery, null);
        }
        if(position < MAX_IMAGE_ON_GALLERY){
            final ImageView imageView = (ImageView)convertView.findViewById(R.id.imgItem);
            imageView.setImageURI(imgs.get(position));
        }
        return convertView;
    }
}
