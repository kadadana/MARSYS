package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import org.postgresql.util.PSQLException;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Repository {
    String url = "jdbc:postgresql://ep-fragrant-term-a9jwf1gb-pooler.gwc.azure.neon.tech:5432/MARSYS_DB?user=neondb_owner&password=npg_KkUHzrI37loY&sslmode=require";


    //Bu metodu çağırırken almak istediğin verinin tablo adı, sütun adı ve id sini veriyorsun.
    //Fonksiyon o verilerle bir select sorgusu çalıştırarak sana geri dönüş yapıyor.
    public String getCellById(String tableName, String columnName, String id) {
        String query = "SELECT \"" + columnName + "\" FROM \"" + tableName + "\" WHERE \"ID\" = ?";
        String cell = null;
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
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

    //Sütun adını ve barkodu vererek INVENTORY tablosunun o sütunundaki değerini alabilirsin
    public String getCellInventoryByBarcode(String columnName, String barcode) {
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

    //STOCK_MOVEMENT tablosun yeni kayıt eklemek için olan metod
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

    //INVOICE_NUMBER sütununu sürekli artıran metod
    public String getLatestInvoiceNumber() {
        String invoiceNumber;
        String query = "SELECT \"INVOICE_NUMBER\" FROM \"INVOICES\" ORDER BY \"INVOICE_NUMBER\" DESC LIMIT 1";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                invoiceNumber = rs.getString("INVOICE_NUMBER");
                return String.format("%06d", Integer.parseInt(invoiceNumber) + 1);

            } else {
                return "000001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "000001";
        }
    }

    //PRODUCT tablosunda QUANTITY değerini azaltan metod
    public void reduceStockQuantity(Product product) {

        for (int p = 0; p < product.getQuantity(); p++) {
            int stockQuantity = Integer.parseInt(getCellInventoryByBarcode("QUANTITY", product.getBarcode()));
            String query = "UPDATE \"INVENTORY\" SET \"QUANTITY\" = " + Integer.toString(stockQuantity - 1) + " WHERE \"BARCODE\" = ?";
            try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, product.getBarcode());

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stockQuantity -= 1;
        }

    }

    //CARD tablosundan bilgi getiren metod
    public String getCardInfo(String column1, String column2, String cell) {
        String returnCell = null;
        String query = "SELECT \"" + column1 + "\" FROM \"CARDS\" WHERE \"" + column2 + "\" = ?";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cell);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                returnCell = rs.getString(column1);
                return returnCell;
            }
            return returnCell;

        } catch (SQLException e) {
            e.printStackTrace();
            return returnCell;
        }
    }

    //Kart bakiyesini düşüren metod
    public void reduceCardBalance(Double paidAmount, String cardNumber) {
        Double firstAmount = Double.parseDouble(getCardInfo("BALANCE", "NUMBER", cardNumber));
        Double lastAmount = firstAmount - paidAmount;

        String query = "UPDATE \"CARDS\" SET \"BALANCE\" = ? WHERE \"NUMBER\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Double.toString(lastAmount));
            stmt.setString(2, cardNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //INVOICE tablosunda yeni fiş kaydı oluşturan metod
    public void insertIntoInvoicesTable(String invoiceNumber, Employee cashier, String paymentType, String cardNumber, String totalAmount, String date) {
        if (paymentType.equals("000000")) {
            paymentType = "CASH";
        } else {
            paymentType = "CARD";
        }

        String query = "INSERT INTO \"INVOICES\" VALUES ( ?, ?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, invoiceNumber);
            stmt.setString(2, paymentType);
            stmt.setString(3, cardNumber);
            stmt.setString(4, totalAmount);
            stmt.setString(5, cashier.getId());
            stmt.setString(6, date);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //CAMPAIGN tablosuna yeni kampanya ekleme metodu
    public void insertIntoCampaignTable(Campaign campaign) {

        String query = "INSERT INTO \"CAMPAIGN\" VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, campaign.getCampaignId());
            stmt.setString(4, campaign.getDiscountType());
            stmt.setString(5, campaign.getDiscountTypeCode());
            stmt.setString(6, campaign.getDiscountValue());
            stmt.setString(7, campaign.getStartDate());
            stmt.setString(8, campaign.getEndDate());
            stmt.setString(9, campaign.getIsActive());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //CAMPAIGN_ID sürekli artıran metod
    public String getLatestCampaignId() {
        String campaignId;
        String query = "SELECT \"CAMPAIGN_ID\" FROM \"CAMPAIGN\" ORDER BY \"CAMPAIGN_ID\" DESC LIMIT 1";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaignId = rs.getString("CAMPAIGN_ID");
                return String.format("%03d", Integer.parseInt(campaignId) + 1);

            } else {
                return "001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "000001";
        }
    }

    //CAMPAIGN tablosundan Campaign modeli veren metod
    public Campaign getCampaignModelById(String id) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"CAMPAIGN_ID\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_VALUE"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;


    }

    public List<Campaign> getAllCampaigns() {
        String query = "SELECT * FROM \"CAMPAIGN\"";
        List<Campaign> campaigns = new ArrayList<>();
        Campaign campaign = null;

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_VALUE"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                campaigns.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return campaigns;
    }
}
