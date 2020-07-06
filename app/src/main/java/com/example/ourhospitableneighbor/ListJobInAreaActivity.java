package com.example.ourhospitableneighbor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.ourhospitableneighbor.model.Job;
import com.example.ourhospitableneighbor.view.PanelItemView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListJobInAreaActivity extends AppCompatActivity {
    private List<Job> jobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_job_in_area);

        jobs = JobService.getInstance().getLastJobsInAreaResult();
        if (jobs == null) jobs = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.listJobInAreaActivity_RecyclerView);
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
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.jobItemView.setJob(jobs.get(position));
        }

        @Override
        public int getItemCount() {
            return jobs.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private PanelItemView jobItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.jobItemView = (PanelItemView) itemView;
        }
    }
}