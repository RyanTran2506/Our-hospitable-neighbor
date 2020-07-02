package com.example.ourhospitableneighbor.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockJob implements JobInterface {
    private String jobID;
    private String jobTitle;
    private JobStatus status;
    private String address;
    private double latitude;
    private double longitude;
    private String ownerID;
    private LocalDate date;
    private List<String> imageIDs;

    public static List<JobInterface> getJobs() {
        List<JobInterface > JOBS = new ArrayList<>();

        JobInterface job = new MockJob();
        job.setLatitude(49.204029);
        job.setLongitude(-122.912883);
        job.setJobTitle("Something something at Douglas College");
        JOBS.add(job);

        job = new MockJob();
        job.setLatitude(49.212926);
        job.setLongitude(-122.922803);
        JOBS.add(job);

        job = new MockJob();
        job.setLatitude(49.215978);
        job.setLongitude(-122.909250);
        JOBS.add(job);

        job = new MockJob();
        job.setLatitude(49.212060);
        job.setLongitude(-122.927357);
        JOBS.add(job);

        job = new MockJob();
        job.setLatitude(49.223533);
        job.setLongitude(-122.932613);
        JOBS.add(job);

        return JOBS;
    }

    @Override
    public String getJobID() {
        return jobID;
    }

    @Override
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    @Override
    public String getJobTitle() {
        return jobTitle;
    }

    @Override
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Override
    public JobStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String getOwnerID() {
        return ownerID;
    }

    @Override
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public List<String> getImageIDs() {
        return imageIDs;
    }

    @Override
    public void setImageIDs(List<String> imageIDs) {
        this.imageIDs = imageIDs;
    }
}
