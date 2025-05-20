package com.marsys.marsys.Models;

public class Coupon {
    private String CouponCode;
    private String DiscountAmount;
    private String StartDate;
    private String EndDate;
    private String IsActive;
    private String Used;
    private String UsingLimit;

    public Coupon(String couponCode, String discountAmount, String startDate, String endDate, String isActive, String used, String usingLimit) {
        this.CouponCode = couponCode;
        this.DiscountAmount = discountAmount;
        this.StartDate = startDate;
        this.EndDate = endDate;
        this.IsActive = isActive;
        this.Used = used;
        this.UsingLimit = usingLimit;

    }

    public String getCouponCode() {
        return CouponCode;
    }

    public void setCouponCode(String couponCode) {
        this.CouponCode = couponCode;
    }

    public String getDiscountAmount() {
        return DiscountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.DiscountAmount = discountAmount;
    }


    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        this.StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        this.EndDate = endDate;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        this.IsActive = isActive;
    }

    public String getUsed() {
        return Used;
    }

    public void setUsed(String used) {
        this.Used = used;
    }

    public String getUsingLimit() {
        return UsingLimit;
    }

    public void setUsingLimit(String usingLimit) {
        this.UsingLimit = usingLimit;
    }
}

