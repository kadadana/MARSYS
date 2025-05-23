package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.StockMovement;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryMete {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured on database.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    public Product getAllStockTable() {
        String query = "SELECT * FROM \"INVENTORY\"";

        Product product = null;
        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                product = new Product(rs.getString("BARCODE"), rs.getString("NAME"), Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")), rs.getString("CATEGORY"), rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")), rs.getString("EXPIRATION"));
            }
            return product;

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting stock table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return product;
    }

    public List<Product> getAllStockList() {
        String query = "SELECT * FROM \"INVENTORY\"";
        List<Product> productList = new ArrayList<>();
        Product product;

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                product = new Product(rs.getString("BARCODE"), rs.getString("NAME"), Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")), rs.getString("CATEGORY"), rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")), rs.getString("EXPIRATION"));
                productList.add(product);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting stock list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return productList;
    }

    //INVENTORY tablosundan belirli sütun ve barcode numarasına göre hücre veren metod
    public String getInventoryCellByBarcode(String columnName, String barcode) {
        String query = "SELECT \"" + columnName + "\" FROM \"INVENTORY\" WHERE \"BARCODE\" = ?";
        String cell = null;
        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cell = rs.getString(columnName);
            }
            return cell;
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured on database processes.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return null;
        }
    }

    //INVENTORY tablosundan Product modeli veren metod
    public Product getProductModelByBarcode(String barcode) {
        Product product = null;

        String query = "SELECT * FROM \"INVENTORY\" WHERE \"BARCODE\" = ?";

        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(rs.getString("BARCODE"), rs.getString("NAME"), Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")), rs.getString("CATEGORY"), rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")), rs.getString("EXPIRATION"));
                return product;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting product model.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return product;
    }

    public void updateStockQuantity(Product product, boolean isReducing) {
        int firstQuantity = Integer.parseInt(getInventoryCellByBarcode("QUANTITY", product.getBarcode()));
        int newQuantity;
        if (isReducing) {
            newQuantity = firstQuantity - product.getQuantity();
        } else {
            newQuantity = firstQuantity + product.getQuantity();

        }


        String query = "UPDATE \"INVENTORY\" SET \"QUANTITY\" = ? WHERE \"BARCODE\" = ?";

        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Integer.toString(newQuantity));
            stmt.setString(2, product.getBarcode());

            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while updating stock quantity.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //STOCK_MOVEMENT tablosun yeni kayıt eklemek için olan metod
    public void insertIntoStockMovementTable(
            String movementId,
            String movementType, Product product,
            String invoiceNumber,
            String user,
            String date) {
        String query = "INSERT INTO \"STOCK_MOVEMENT\" VALUES ( ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, movementId);
            stmt.setString(2, movementType);
            stmt.setString(3, product.getBarcode());
            stmt.setString(9, invoiceNumber);
            stmt.setString(10, user);
            stmt.setString(11, date);

            stmt.executeUpdate();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while inserting into stock movement table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    public List<StockMovement> getStockMovementList() {
        String query = "SELECT * FROM \"STOCK_MOVEMENT\" ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
        List<StockMovement> stockMovementList = new ArrayList<>();
        StockMovement stockMovement;

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                stockMovement = new StockMovement(
                        rs.getString("MOVEMENT_ID"),
                        rs.getString("MOVEMENT_TYPE"),
                        rs.getString("BARCODE"),
                        rs.getString("INVOICE_NUMBER"),
                        rs.getString("USER"),
                        rs.getString("DATE"));
                stockMovementList.add(stockMovement);
            }
            return stockMovementList;
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting stock movement list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return stockMovementList;
    }

    public List<StockMovement> getStockMovementListBySearching(String method, String firstDate, String lastDate, String barcode, String invoiceNumber) {
        List<StockMovement> stockMovementList = new ArrayList<>();

        String query;

        switch (method) {
            case "01":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" " +
                        "WHERE TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') AND TO_DATE(?, 'MM-DD-YYYY') " +
                        "AND \"BARCODE\" = ? AND \"INVOICE_NUMBER\" = ? ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
                break;
            case "02":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" " +
                        "WHERE TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') AND TO_DATE(?, 'MM-DD-YYYY') " +
                        "AND \"BARCODE\" = ?  ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
                break;
            case "03":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" " +
                        "WHERE TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') AND TO_DATE(?, 'MM-DD-YYYY') " +
                        "AND \"INVOICE_NUMBER\" = ?  ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
                break;
            case "04":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" " +
                        "WHERE TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') AND TO_DATE(?, 'MM-DD-YYYY') " +
                        " ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
                break;
            case "05":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" WHERE " +
                        "\"BARCODE\" = ? AND \"INVOICE_NUMBER\" = ? " +
                        " ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
                break;
            case "06":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" WHERE " +
                        "\"BARCODE\" = ? ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC ";
                break;
            case "07":
                query = "SELECT * FROM \"STOCK_MOVEMENT\" WHERE " +
                        "\"INVOICE_NUMBER\" = ? ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC  ";
                break;
            default:
                return getStockMovementList();

        }

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            switch (method) {
                case "01":
                    stmt.setString(1, firstDate);
                    stmt.setString(2, lastDate);
                    stmt.setString(3, barcode);
                    stmt.setString(4, invoiceNumber);
                    break;
                case "02":
                    stmt.setString(1, firstDate);
                    stmt.setString(2, lastDate);
                    stmt.setString(3, barcode);
                    break;
                case "03":
                    stmt.setString(1, firstDate);
                    stmt.setString(2, lastDate);
                    stmt.setString(3, invoiceNumber);
                    break;
                case "04":
                    stmt.setString(1, firstDate);
                    stmt.setString(2, lastDate);
                    break;
                case "05":
                    stmt.setString(1, barcode);
                    stmt.setString(2, invoiceNumber);
                    break;
                case "06":
                    stmt.setString(1, barcode);
                    break;
                case "07":
                    stmt.setString(1, invoiceNumber);
                    break;
                default:
                    return getStockMovementList();

            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StockMovement stockMovement = new StockMovement(
                        rs.getString("MOVEMENT_ID"),
                        rs.getString("MOVEMENT_TYPE"),
                        rs.getString("BARCODE"),
                        rs.getString("INVOICE_NUMBER"),
                        rs.getString("USER"),
                        rs.getString("DATE"));
                stockMovementList.add(stockMovement);
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting stock movement list by searching.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }

        return stockMovementList;
    }

    public String getLatestMovementId() {
        String invoiceNumber;
        String query = "SELECT \"MOVEMENT_ID\" FROM \"STOCK_MOVEMENT\" ORDER BY \"MOVEMENT_ID\" DESC LIMIT 1";
        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                invoiceNumber = rs.getString("MOVEMENT_ID");
                return String.format("%06d", Integer.parseInt(invoiceNumber) + 1);

            } else {
                return "000001";
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting latest movement id.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return "000001";
        }
    }
}
