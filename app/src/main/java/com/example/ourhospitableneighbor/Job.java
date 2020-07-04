package com.example.ourhospitableneighbor;

public class Job {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String status;
    private int paymentType;  //assume that payPerHour(type = 0), payPerFinishJob(type = 1)
    private int expectedHrs;    //How long to finish the job
    private double rate;    //rate per hrs
    private double totalPay;    //Total payment amt

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
