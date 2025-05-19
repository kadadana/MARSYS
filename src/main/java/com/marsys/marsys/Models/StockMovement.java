package com.marsys.marsys.Models;

public class StockMovement {

    private String MovementId;
    private String MovementType;
    private String Barcode;
    private String InvoiceNumber;
    private String User;
    private String Date;

    public StockMovement(String movementId,
                         String movementType,
                         String barcode,
                         String invoiceNumber,
                         String user,
                         String date) {
        this.MovementId = movementId;
        this.MovementType = movementType;
        this.Barcode = barcode;
        this.InvoiceNumber = invoiceNumber;
        this.User = user;
        this.Date = date;
    }

    public String getMovementId() {
        return MovementId;
    }

    public void setMovementId(String movementId) {
        MovementId = movementId;
    }

    public String getMovementType() {
        return MovementType;
    }

    public void setMovementType(String movementType) {
        MovementType = movementType;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.InvoiceNumber = invoiceNumber;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

}
