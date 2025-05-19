package com.marsys.marsys.Models;

public class Product {
    private String Barcode;
    private String ProductName;
    private int Quantity;
    private double Price;
    private String Category;
    private String Brand;
    private double BuyingPrice;
    private String ExpirationDate;
    private boolean discounted = false;
    private double discountedPrice = getPrice();

    public Product(String barcode, String productName, int quantity, double price, String category,
                   String brand, double buyingPrice, String expirationDate) {
        this.Barcode = barcode;
        this.ProductName = productName;
        this.Quantity = quantity;
        this.Price = price;
        this.Category = category;
        this.Brand = brand;
        this.BuyingPrice = buyingPrice;
        this.ExpirationDate = expirationDate;
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

    public boolean isDiscounted() {
        return discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

}
