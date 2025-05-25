package com.marsys.marsys.Helpers;

public class DailyInvoiceReport {
    private String date;
    private double revenue;
    private double cost;
    private double profit;
    private double margin;

    public DailyInvoiceReport(String date, double revenue, double cost, double profit, double margin) {
        this.date = date;
        this.revenue = revenue;
        this.cost = cost;
        this.profit = profit;
        this.margin = margin;
    }

    // Getter ve Setter metodları
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

}
