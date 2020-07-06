package com.example.ourhospitableneighbor.model;

import android.location.Location;

import java.time.LocalDate;
import java.util.List;

public class Job {
    private String jobID;
    private String jobTitle;
    private JobStatus status;
    private String address;
    private double latitude;
    private double longitude;
    private String ownerID;
    private LocalDate date;
    private List<String> imageIDs;
    private int paymentType;  //assume that payPerHour(type = 0), payPerFinishJob(type = 1)
    private int expectedHrs;    //How long to finish the job
    private double rate;    //rate per hrs
    private double totalPay;    //Total payment amt
    private Float distanceFromUserLocation;

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getImageIDs() {
        return imageIDs;
    }

    public void setImageIDs(List<String> imageIDs) {
        this.imageIDs = imageIDs;
    }

    public String getThumbnail() {
        List<String> imageIDs = getImageIDs();
        if (imageIDs == null || imageIDs.isEmpty()) return null;
        return imageIDs.get(0);
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getExpectedHrs() {
        return expectedHrs;
    }

    public void setExpectedHrs(int expectedHrs) {
        this.expectedHrs = expectedHrs;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(double totalPay) {
        this.totalPay = totalPay;
    }

    public void setUserCurrentLocation(Location location) {
        if (location == null) return;
        distanceFromUserLocation = getDistanceFromLocation(location);
    }

    public Float getDistanceFromUserLocation() {
        return this.distanceFromUserLocation;
    }

    private float getDistanceFromLocation(Location location) {
        float[] result = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), this.getLatitude(), this.getLongitude(), result);
        return result[0];
    }
}
