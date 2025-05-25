package com.marsys.marsys.Helpers;

public class DailyPaymentSummary {
    private String saleDate;
    private double cardAmount;
    private double cashAmount;

    public DailyPaymentSummary(String saleDate, double cardAmount, double cashAmount) {
        this.saleDate = saleDate;
        this.cardAmount = cardAmount;
        this.cashAmount = cashAmount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public double getCardAmount() {
        return cardAmount;
    }

    public double getCashAmount() {
        return cashAmount;
    }
}