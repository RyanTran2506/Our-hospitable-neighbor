package com.example.ourhospitableneighbor.model;

import java.time.LocalDate;
import java.util.List;

public interface JobInterface {
    String getJobID();

    void setJobID(String jobID);

    String getJobTitle();

    void setJobTitle(String jobTitle);

    JobStatus getStatus();

    void setStatus(JobStatus status);

    String getAddress();

    void setAddress(String address);

    double getLatitude();

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);

    String getOwnerID();

    void setOwnerID(String ownerID);

    LocalDate getDate();

    void setDate(LocalDate date);

    List<String> getImageIDs();

    void setImageIDs(List<String> ids);

    String getThumbnail();
}
