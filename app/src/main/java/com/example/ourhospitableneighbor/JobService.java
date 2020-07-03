package com.example.ourhospitableneighbor;

import com.example.ourhospitableneighbor.model.Job;
import com.example.ourhospitableneighbor.model.JobInterface;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JobService {
    private static JobService instance;
    private CollectionReference collection;
    private List<JobInterface> jobs = new ArrayList<>();

    public static JobService getInstance() {
        if (instance == null) instance = new JobService();
        return instance;
    }

    private JobService() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collection = db.collection("jobs");
    }

    public Task<List<JobInterface>> getAllJobs() {
        return collection.get().continueWith(task -> {
            List<DocumentSnapshot> documents = Objects.requireNonNull(task.getResult()).getDocuments();
            jobs.clear();
            for (DocumentSnapshot doc : documents) {
                jobs.add(mapDocumentToJob(doc));
            }
            return jobs;
        });
    }

    private JobInterface mapDocumentToJob(DocumentSnapshot doc) {
        Job job = new Job();
        job.setJobTitle(doc.getString("title"));
        job.setAddress(doc.getString("address"));
        job.setOwnerID(doc.getString("ownerID"));
        job.setImageIDs((List<String>) doc.get("images"));

        GeoPoint coords = doc.getGeoPoint("coords");
        job.setLatitude(coords.getLatitude());
        job.setLongitude(coords.getLongitude());
        return job;
    }
}
