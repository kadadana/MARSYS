package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Coupon;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public Employee getEmployeeModelById(String id) {
        Employee employee = null;
        String query = "SELECT * FROM \"EMPLOYEE\" WHERE \"ID\" = ?";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                employee = new Employee(rs.getString("NAME"), rs.getString("LAST_NAME"), rs.getString("POSITION"),
                        rs.getString("ID"), rs.getString("PASSWORD"), rs.getString("STORE_CODE"));
                return employee;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
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
            int stockQuantity = Integer.parseInt(getInventoryCellByBarcode("QUANTITY", product.getBarcode()));
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
            stmt.setString(2, campaign.getDiscountType());
            stmt.setString(3, campaign.getDiscountTypeCode());
            stmt.setString(4, campaign.getDiscountFor());
            stmt.setString(5, campaign.getStartDate());
            stmt.setString(6, campaign.getEndDate());
            stmt.setString(7, campaign.getIsActive());

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
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;


    }

    //CAMPAIGN tablosundaki her satırı List olarak veren metod
    public List<Campaign> getCampaignList() {
        String query = "SELECT * FROM \"CAMPAIGN\"";
        List<Campaign> campaignList = new ArrayList<>();
        Campaign campaign;

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                campaignList.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaignList;
    }

    //CAMPAIGN tablosundaki bir satırı değiştiren metod
    public void updateCampaignTable(Campaign campaign) {
        String query = "UPDATE \"CAMPAIGN\" SET " +
                "\"CAMPAIGN_ID\" = ?, " +
                "\"DISCOUNT_TYPE\" = ?, " +
                "\"DISCOUNT_TYPE_CODE\" = ?, " +
                "\"DISCOUNT_FOR\" = ?, " +
                "\"START_DATE\" = ?, " +
                "\"END_DATE\" = ?, " +
                "\"IS_ACTIVE\" = ? " +
                "WHERE \"CAMPAIGN_ID\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, campaign.getCampaignId());
            stmt.setString(2, campaign.getDiscountType());
            stmt.setString(3, campaign.getDiscountTypeCode());
            stmt.setString(4, campaign.getDiscountFor());
            stmt.setString(5, campaign.getStartDate());
            stmt.setString(6, campaign.getEndDate());
            stmt.setString(7, campaign.getIsActive());
            stmt.setString(8, campaign.getCampaignId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //CAMPAIGN tablosundan kayıt silme metodu
    public void deleteFromCampaignById(String campaignId) {
        String query = "DELETE FROM \"CAMPAIGN\" WHERE \"CAMPAIGN_ID\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, campaignId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Campaign> getActiveCampaigns() {
        String query = "SELECT * FROM \"CAMPAIGN\" " +
                "WHERE \"IS_ACTIVE\" = 'ACTIVE'" +
                "  AND CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')\n" +
                "                      AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY') ";
        List<Campaign> campaignList = new ArrayList<>();
        Campaign campaign;

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                campaignList.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaignList;
    }

    public Campaign getCampaignModelByDiscountFor(String discountFor) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, discountFor);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;
    }

    public Campaign getBuy2Get1CampaignByBarcode(String barcode) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ? AND \"DISCOUNT_TYPE_CODE\" = '01' AND \"IS_ACTIVE\" = 'ACTIVE' AND " +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;
    }

    public Campaign get50CampaignForCategory(String category) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ? AND \"DISCOUNT_TYPE_CODE\" = '03' AND \"IS_ACTIVE\" = 'ACTIVE' AND " +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;
    }

    public Campaign get50CampaignForProduct(String barcode) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ? AND \"DISCOUNT_TYPE_CODE\" = '02' AND \"IS_ACTIVE\" = 'ACTIVE' AND " +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(rs.getString("CAMPAIGN_ID"), rs.getString("DISCOUNT_TYPE"), rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;
    }

    public Coupon getCouponModelByCode(String code) {
        Coupon coupon = null;

        String query = "SELECT * FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                coupon = new Coupon(rs.getString("COUPON_CODE"), rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"),
                        rs.getString("USED"));
                return coupon;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coupon;
    }

    public List<Coupon> getCouponList() {
        String query = "SELECT * FROM \"COUPON\"";
        List<Coupon> couponList = new ArrayList<>();
        Coupon coupon;

        try (var conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                coupon = new Coupon(rs.getString("COUPON_CODE"), rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("IS_ACTIVE"),
                        rs.getString("USED"));
                couponList.add(coupon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couponList;
    }

    public void updateCouponTable(Coupon coupon) {
        String query = "UPDATE \"COUPON\" SET " +
                "\"COUPON_CODE\" = ?, " +
                "\"DISCOUNT_AMOUNT\" = ?, " +
                "\"START_DATE\" = ?, " +
                "\"END_DATE\" = ?, " +
                "\"IS_ACTIVE\" = ? " +
                "WHERE \"COUPON_CODE\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, coupon.getCouponCode());
            stmt.setString(2, coupon.getDiscountAmount());
            stmt.setString(3, coupon.getStartDate());
            stmt.setString(4, coupon.getEndDate());
            stmt.setString(5, coupon.getIsActive());
            stmt.setString(6, coupon.getCouponCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromCouponbyCode(String couponCode) {
        String query = "DELETE FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, couponCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoCouponTable(Coupon coupon) {
        String query = "INSERT INTO \"COUPON\" VALUES (?, ?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, coupon.getCouponCode());
            stmt.setString(2, coupon.getDiscountAmount());
            stmt.setString(3, coupon.getStartDate());
            stmt.setString(4, coupon.getEndDate());
            stmt.setString(5, coupon.getIsActive());
            stmt.setString(6, "0");

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLatestCouponCode() {
        String couponCode;
        String query = "SELECT \"COUPON_CODE\" FROM \"COUPON\" ORDER BY \"COUPON_CODE\" DESC LIMIT 1";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                couponCode = rs.getString("COUPON_CODE");
                return String.format("%03d", Integer.parseInt(couponCode) + 1);

            } else {
                return "001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "001";
        }
    }

    public Coupon getValidCoupon(String couponCode) {
        Coupon coupon = null;
        String query = "SELECT * FROM \"COUPON\" " +
                "WHERE \"COUPON_CODE\" = ? AND \"IS_ACTIVE\" = 'ACTIVE' AND" +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, couponCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                coupon = new Coupon(rs.getString("COUPON_CODE"),
                        rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"),
                        rs.getString("USED"));
                return coupon;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coupon;

    }

    public void updateCouponUsed(String couponCode) {
        String _query = "SELECT \"USED\" FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(_query)) {
            stmt.setString(1, couponCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int preUsed = Integer.parseInt(rs.getString("USED"));
                int newUsed = preUsed + 1;
                String query = "UPDATE \"COUPON\" SET \"USED\" = ? WHERE \"COUPON_CODE\" = ?";
                PreparedStatement stmt2 = conn.prepareStatement(query);
                stmt2.setString(1, Integer.toString(newUsed));
                stmt2.setString(2, couponCode);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCouponUsed(String couponCode) {
        String used = "";
        String query = "SELECT \"USED\" FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, couponCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                used = rs.getString("USED");
                return used;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return used;
    }
}
