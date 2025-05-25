package com.marsys.marsys.Helpers;

public class DailyRevenue {
    private String date;
    private double revenue;

    public DailyRevenue(String date, double revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public String getDate() {
        return date;
    }

    public double getRevenue() {
        return revenue;
    }
}