package com.marsys.marsys.Helpers;

public class TopStaffs {
    private String staffName;
    private double staffSales;
    private int staffTransactions;

    public TopStaffs(String staffName, double staffSales, int staffTransactions) {
        this.staffName = staffName;
        this.staffSales = staffSales;
        this.staffTransactions = staffTransactions;
    }

    // Getter ve Setter metodları
    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public double getStaffSales() {
        return staffSales;
    }

    public void setStaffSales(double staffSales) {
        this.staffSales = staffSales;
    }

    public int getStaffTransactions() {
        return staffTransactions;
    }

    public void setStaffTransactions(int staffTransactions) {
        this.staffTransactions = staffTransactions;
    }


}
