package com.marsys.marsys.Helpers;

public class TopProducts {
    private String ProductName;
    private Integer Quantity;
    private Double TotalSales;

    public TopProducts(String productName, Integer quantity, Double totalSales) {
        this.ProductName = productName;
        this.Quantity = quantity;
        this.TotalSales = totalSales;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        this.Quantity = quantity;
    }

    public Double getTotalSales() {
        return TotalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.TotalSales = totalSales;
    }
}
