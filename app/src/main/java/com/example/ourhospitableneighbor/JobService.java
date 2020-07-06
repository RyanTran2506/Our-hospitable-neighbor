package com.example.ourhospitableneighbor;

import android.location.Location;

import com.example.ourhospitableneighbor.model.Job;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JobService {
    private static JobService instance;
    private CollectionReference collection; // firebase storage
    private Location userCurrentLocation;
    private List<Job> jobs;
    private List<Job> lastJobsInAreaResult;
    private Task<List<Job>> getAllJobsTask;

    public static JobService getInstance() {
        if (instance == null) instance = new JobService();
        return instance;
    }

    private JobService() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collection = db.collection("jobs");
    }

    public Task<List<Job>> getAllJobs() {
        if (jobs != null) return Tasks.call(() -> jobs);
        if (getAllJobsTask == null) {
            getAllJobsTask = collection.get().continueWith(task -> {
                List<DocumentSnapshot> documents = Objects.requireNonNull(task.getResult()).getDocuments();
                jobs = new ArrayList<>();
                for (DocumentSnapshot doc : documents) {
                    jobs.add(mapDocumentToJob(doc));
                }
                getAllJobsTask = null;
                return jobs;
            });
        }
        return getAllJobsTask;
    }

    public Task<List<Job>> getJobsInArea(LatLngBounds area) {
        return getAllJobs().continueWith(task -> {
            List<Job> allJobs = task.getResult();
            List<Job> jobsInArea = new ArrayList<>();

            for (Job job : allJobs) {
                double lat = job.getLatitude();
                double lng = job.getLongitude();
                if (lat >= area.southwest.latitude && lat <= area.northeast.latitude && lng >= area.southwest.longitude && lng <= area.northeast.longitude) {
                    jobsInArea.add(job);
                    job.setUserCurrentLocation(userCurrentLocation);
                }
            }

            // Have to do this check else the sort function will never complete
            if (userCurrentLocation != null) {
                Collections.sort(jobsInArea, (o1, o2) -> Float.compare(o1.getDistanceFromUserLocation(), o2.getDistanceFromUserLocation()));
            }

            return lastJobsInAreaResult = jobsInArea;
        });
    }

    public List<Job> getLastJobsInAreaResult() {
        return lastJobsInAreaResult;
    }

    public void setUserCurrentLocation(Location location) {
        this.userCurrentLocation = location;
    }

    private Job mapDocumentToJob(DocumentSnapshot doc) {
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
