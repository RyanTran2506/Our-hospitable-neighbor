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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    private List<Post> ownedPosts, takenPosts;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ownedPosts = PostService.getInstance().getOwnedPost(userID);
        if (ownedPosts == null) ownedPosts = new ArrayList<>();

        RecyclerView ownedRecyclerView = findViewById(R.id.History_Owned_RecyclerView);
        ownedRecyclerView.setAdapter(new OwnedAdapter());
        ownedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        takenPosts = PostService.getInstance().getTakenPosts(userID);
        if (takenPosts == null) takenPosts = new ArrayList<>();

        RecyclerView takenRecyclerView = findViewById(R.id.History_Taken_RecyclerView);
        takenRecyclerView.setAdapter(new TakenAdapter());
        takenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class OwnedAdapter extends RecyclerView.Adapter<OwnedViewHolder> {
        @NonNull
        @Override
        public OwnedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OwnedViewHolder(new PanelItemView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull OwnedViewHolder holder, int position) {
            holder.postItemView.setPost(ownedPosts.get(position));
        }

        @Override
        public int getItemCount() {
            return ownedPosts.size();
        }
    }

    private class TakenAdapter extends RecyclerView.Adapter<TakenViewHolder> {
        @NonNull
        @Override
        public TakenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TakenViewHolder(new PanelItemView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull TakenViewHolder holder, int position) {
            holder.postItemView.setPost(takenPosts.get(position));
        }

        @Override
        public int getItemCount() {
            return takenPosts.size();
        }
    }

    private static class OwnedViewHolder extends RecyclerView.ViewHolder {
        private PanelItemView postItemView;

        public OwnedViewHolder(@NonNull View itemView) {
            super(itemView);
            this.postItemView = (PanelItemView) itemView;
        }
    }

    private static class TakenViewHolder extends RecyclerView.ViewHolder {
        private PanelItemView postItemView;

        public TakenViewHolder(@NonNull View itemView) {
            super(itemView);
            this.postItemView = (PanelItemView) itemView;
        }
    }
}