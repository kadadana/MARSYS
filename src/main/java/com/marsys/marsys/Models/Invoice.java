package com.marsys.marsys.Models;

public class Invoice {

    private String invoiceNumber;
    private String paymentType;
    private String cardNumber;
    private String paidAmount;
    private String discountAmount;
    private String actualCartAmount;
    private String cashierId;
    private String date;
    private String originalInvoiceNumber;

    public Invoice(String invoiceNumber, String paymentType, String cardNumber,
                   String paidAmount, String discountAmount, String actualCartAmount,
                   String cashierId, String date, String originalInvoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        this.paymentType = paymentType;
        this.cardNumber = cardNumber;
        this.paidAmount = paidAmount;
        this.discountAmount = discountAmount;
        this.actualCartAmount = actualCartAmount;
        this.cashierId = cashierId;
        this.date = date;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getActualCartAmount() {
        return actualCartAmount;
    }

    public void setActualCartAmount(String actualCartAmount) {
        this.actualCartAmount = actualCartAmount;
    }

    public String getCashierId() {
        return cashierId;
    }

    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOriginalInvoiceNumber() {
        return originalInvoiceNumber;
    }

    public void setOriginalInvoiceNumber(String originalInvoiceNumber) {
        this.originalInvoiceNumber = originalInvoiceNumber;
    }


}
