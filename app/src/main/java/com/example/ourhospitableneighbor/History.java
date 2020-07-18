package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.example.ourhospitableneighbor.model.Post;
import com.example.ourhospitableneighbor.view.PanelItemView;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    private List<Post> posts;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //temp, change it back later - Ryan
        userID = "Ryan";

        posts = PostService.getInstance().getPostHistory(userID);
        if (posts == null) posts = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.History_RecyclerView);
        recyclerView.setAdapter(new Adapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(new PanelItemView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull History.ViewHolder holder, int position) {
            holder.postItemView.setPost(posts.get(position));
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private PanelItemView postItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.postItemView = (PanelItemView) itemView;
        }
    }
}