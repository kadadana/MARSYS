package com.marsys.marsys.Models;

import javafx.beans.property.SimpleStringProperty;

public class Product {
    private String Barcode;
    private String ProductName;
    private int Quantity;
    private double Price;
    private String Category;
    private String Brand;
    private double BuyingPrice;
    private String ExpirationDate;
    private String TaxRate;
    private String DiscountRate;


    public Product(String barcode, String productName, int quantity, double price, String category,
                   String brand, double buyingPrice, String expirationDate, String taxRate, String discountRate) {
        this.Barcode = barcode;
        this.ProductName = productName;
        this.Quantity = quantity;
        this.Price = price;
        this.Category = category;
        this.Brand = brand;
        this.BuyingPrice = buyingPrice;
        this.ExpirationDate = expirationDate;
        this.TaxRate = taxRate;
        this.DiscountRate = discountRate;
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

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
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

    public double getBuyingPrice() {
        return BuyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        BuyingPrice = buyingPrice;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getTaxRate() {
        return TaxRate;
    }

    public void setTaxRate(String taxRate) {
        TaxRate = taxRate;
    }

    public String getDiscountRate() {
        return DiscountRate;
    }

    public void setDiscountRate(String discountRate) {
        DiscountRate = discountRate;
    }
}
