package com.marsys.marsys.Models;

public class Campaign {
    private String CampaignId;
    private String DiscountType;
    private String DiscountTypeCode;
    private String DiscountFor;
    private String StartDate;
    private String EndDate;
    private String IsActive;

    public Campaign(String campaignId, String discountType, String discountTypeCode, String discountFor, String startDate, String endDate, String isActive) {
        this.CampaignId = campaignId;
        this.DiscountType = discountType;
        this.DiscountTypeCode = discountTypeCode;
        this.DiscountFor = discountFor;
        this.StartDate = startDate;
        this.EndDate = endDate;
        this.IsActive = isActive;
    }

    public String getCampaignId() {
        return CampaignId;
    }

    public void setCampaignId(String campaignId) {
        this.CampaignId = campaignId;
    }

    public String getDiscountType() {
        return DiscountType;
    }

    public void setDiscountType(String discountType) {
        this.DiscountType = discountType;
    }

    public String getDiscountTypeCode() {
        return DiscountTypeCode;
    }

    public void setDiscountTypeCode(String discountTypeCode) {
        this.DiscountTypeCode = discountTypeCode;
    }

    public String getDiscountFor() {
        return DiscountFor;
    }

    public void setDiscountValue(String discountFor) {
        this.DiscountFor = discountFor;
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

