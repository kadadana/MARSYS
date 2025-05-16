package com.marsys.marsys.Models;

public class StockMovement {
    private String Barcode;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Category;
    private String Brand;
    private String BuyingPrice;
    private String ExpirationDate;
    private String MovementType;
    private String InvoiceNumber;
    private String User;
    private String Date;

    public StockMovement(String barcode, String productName, String quantity, String price, String category,
                         String brand, String buyingPrice, String expirationDate, String movementType,
                         String invoiceNumber, String user, String date) {
        this.Barcode = barcode;
        this.ProductName = productName;
        this.Quantity = quantity;
        this.Price = price;
        this.Category = category;
        this.Brand = brand;
        this.BuyingPrice = buyingPrice;
        this.ExpirationDate = expirationDate;
        this.MovementType = movementType;
        this.InvoiceNumber = invoiceNumber;
        this.User = user;
        this.Date = date;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getBuyingPrice() {
        return BuyingPrice;
    }

    public void setBuyingPrice(String buyingPrice) {
        BuyingPrice = buyingPrice;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getMovementType() {
        return MovementType;
    }

    public void setMovementType(String movementType) {
        MovementType = movementType;
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
