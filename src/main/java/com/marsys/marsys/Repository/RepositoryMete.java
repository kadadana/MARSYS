package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.StockMovement;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryMete {
    String url = "jdbc:postgresql://ep-fragrant-term-a9jwf1gb-pooler.gwc.azure.neon.tech:5432/MARSYS_DB?user=neondb_owner&password=npg_KkUHzrI37loY&sslmode=require";

    public Product getAllStockTable() {
        String query = "SELECT * FROM \"INVENTORY\"";

        Product product = null;
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                product = new Product(rs.getString("BARCODE"), rs.getString("NAME"), Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")), rs.getString("CATEGORY"), rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")), rs.getString("EXPIRATION"), rs.getString("TAX_RATE"));
            }
            return product;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public List<Product> getAllStockList() {
        String query = "SELECT * FROM \"INVENTORY\"";
        List<Product> productList = new ArrayList<>();
        Product product;

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                product = new Product(rs.getString("BARCODE"), rs.getString("NAME"), Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")), rs.getString("CATEGORY"), rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")), rs.getString("EXPIRATION"), rs.getString("TAX_RATE"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    //INVENTORY tablosundan belirli sütun ve barcode numarasına göre hücre veren metod
    public String getInventoryCellByBarcode(String columnName, String barcode) {
        String query = "SELECT \"" + columnName + "\" FROM \"INVENTORY\" WHERE \"BARCODE\" = ?";
        String cell = null;
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cell = rs.getString(columnName);
            }
            return cell;
        } catch (SQLException e) {
            e.printStackTrace();  // Hata mesajını konsola yazdır
            return null;
        }
    }

    //INVENTORY tablosundan Product modeli veren metod
    public Product getProductModelByBarcode(String barcode) {
        Product product = null;

        String query = "SELECT * FROM \"INVENTORY\" WHERE \"BARCODE\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(rs.getString("BARCODE"), rs.getString("NAME"), Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")), rs.getString("CATEGORY"), rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")), rs.getString("EXPIRATION"), rs.getString("TAX_RATE"));
                return product;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public void updateStockQuantity(Product product) {

        Integer firstQuantity = Integer.parseInt(getInventoryCellByBarcode("QUANTITY", product.getBarcode()));
        Integer newQuantity = firstQuantity + product.getQuantity();//18

        String query = "UPDATE \"INVENTORY\" SET \"QUANTITY\" = ? WHERE \"BARCODE\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Integer.toString(newQuantity));
            stmt.setString(2, product.getBarcode());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoStockMovementTable(Product product, String movementType, String invoiceNumber, String user, String date) {
        String query = "INSERT INTO \"STOCK_MOVEMENT\" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getBarcode());
            stmt.setString(2, product.getProductName());
            stmt.setString(3, Integer.toString(product.getQuantity()));
            stmt.setString(4, Double.toString(product.getPrice()));
            stmt.setString(5, product.getCategory());
            stmt.setString(6, product.getBrand());
            stmt.setString(7, Double.toString(product.getBuyingPrice()));
            stmt.setString(8, product.getExpirationDate());
            stmt.setString(9, movementType);
            stmt.setString(10, invoiceNumber);
            stmt.setString(11, user);
            stmt.setString(12, date);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StockMovement> getStockMovementList() {
        String query = "SELECT * FROM \"STOCK_MOVEMENT\"";
        List<StockMovement> stockMovementList = new ArrayList<>();
        StockMovement stockMovement;

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                stockMovement = new StockMovement(rs.getString("BARCODE"),
                        rs.getString("NAME"),
                        rs.getString("QUANTITY"),
                        rs.getString("SALE_PRICE"),
                        rs.getString("CATEGORY"),
                        rs.getString("BRAND"),
                        rs.getString("BUYING_PRICE"),
                        rs.getString("EXPIRATION"),
                        rs.getString("MOVEMENT_TYPE"),
                        rs.getString("INVOICE_NUMBER"),
                        rs.getString("USER"),
                        rs.getString("DATE"));
                stockMovementList.add(stockMovement);
            }
            return stockMovementList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockMovementList;
    }

    public List<StockMovement> getStockMovementListBySearch(String method, String firstDate, String lastDate, String barcode) {
        List<StockMovement> stockMovementList = new ArrayList<>();

        String query = "";
        boolean searchByDate = method.equals("DATE") || method.equals("BOTH");
        boolean searchByBarcode = method.equals("BARCODE") || method.equals("BOTH");

        if (method.equals("DATE")) {
            query = "SELECT * FROM \"STOCK_MOVEMENT\" " +
                    "WHERE TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') AND TO_DATE(?, 'MM-DD-YYYY')";
        } else if (method.equals("BARCODE")) {
            query = "SELECT * FROM \"STOCK_MOVEMENT\" WHERE \"BARCODE\" = ?";
        } else if (method.equals("BOTH")) {
            query = "SELECT * FROM \"STOCK_MOVEMENT\" " +
                    "WHERE TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') AND TO_DATE(?, 'MM-DD-YYYY') " +
                    "AND \"BARCODE\" = ?";
        } else {
            return getStockMovementList(); // default dönüş
        }

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (method.equals("DATE")) {
                stmt.setString(1, firstDate);
                stmt.setString(2, lastDate);
            } else if (method.equals("BARCODE")) {
                stmt.setString(1, barcode);
            } else if (method.equals("BOTH")) {
                stmt.setString(1, firstDate);
                stmt.setString(2, lastDate);
                stmt.setString(3, barcode);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StockMovement stockMovement = new StockMovement(
                        rs.getString("BARCODE"),
                        rs.getString("NAME"),
                        rs.getString("QUANTITY"),
                        rs.getString("SALE_PRICE"),
                        rs.getString("CATEGORY"),
                        rs.getString("BRAND"),
                        rs.getString("BUYING_PRICE"),
                        rs.getString("EXPIRATION"),
                        rs.getString("MOVEMENT_TYPE"),
                        rs.getString("INVOICE_NUMBER"),
                        rs.getString("USER"),
                        rs.getString("DATE"));
                stockMovementList.add(stockMovement);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stockMovementList;
    }


}
