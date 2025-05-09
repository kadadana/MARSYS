package com.marsys.marsys.Models;

public class Coupon {
    private String CouponCode;
    private String DiscountAmount;
    private String StartDate;
    private String EndDate;
    private String IsActive;

    public Coupon(String couponCode, String discountAmount, String startDate, String endDate, String isActive) {
        this.CouponCode = couponCode;
        this.DiscountAmount = discountAmount;
        this.StartDate = startDate;
        this.EndDate = endDate;
        this.IsActive = isActive;
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
}

