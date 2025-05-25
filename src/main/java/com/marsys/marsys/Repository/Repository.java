package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.*;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {

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

    //Bu metodu çağırırken almak istediğin verinin tablo adı, sütun adı ve id sini veriyorsun.
    //Fonksiyon o verilerle bir select sorgusu çalıştırarak sana geri dönüş yapıyor.
    public String getCellById(String tableName, String columnName, String id) {
        String query = "SELECT \"" + columnName + "\" FROM \"" + tableName + "\" WHERE \"ID\" = ?";
        String cell = null;
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
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

    public Employee getEmployeeModelById(String id) {
        Employee employee = null;
        String query = "SELECT * FROM \"EMPLOYEE\" WHERE \"ID\" = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                employee = new Employee(
                        rs.getString("NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("POSITION"),
                        rs.getString("ID"),
                        rs.getString("PASSWORD"),
                        rs.getString("STORE_CODE"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("PASSWORD"),
                        rs.getString("COUPON_CODE"));
                return employee;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting employee model.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return employee;
    }

    //INVENTORY tablosundan belirli sütun ve barcode numarasına göre hücre veren metod
    public String getInventoryCellByBarcode(String columnName, String barcode) {
        String query = "SELECT \"" + columnName + "\" FROM \"INVENTORY\" WHERE \"BARCODE\" = ?";
        String cell = null;
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cell = rs.getString(columnName);
            }
            return cell;
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting inventory cell.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return null;
        }
    }

    //INVENTORY tablosundan Product modeli veren metod
    public Product getProductModelByBarcode(String barcode) {
        Product product = null;

        String query = "SELECT * FROM \"INVENTORY\" WHERE \"BARCODE\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(
                        rs.getString("BARCODE"),
                        rs.getString("NAME"),
                        Integer.parseInt(rs.getString("QUANTITY")),
                        Double.parseDouble(rs.getString("SALE_PRICE")),
                        rs.getString("CATEGORY"),
                        rs.getString("BRAND"),
                        Double.parseDouble(rs.getString("BUYING_PRICE")),
                        rs.getString("EXPIRATION"));
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

    //STOCK_MOVEMENT tablosun yeni kayıt eklemek için olan metod
    public void insertIntoStockMovementTable(StockMovement stockMovement) {
        String query = "INSERT INTO \"STOCK_MOVEMENT\" VALUES ( ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stockMovement.getMovementId());
            stmt.setString(2, stockMovement.getMovementType());
            stmt.setString(3, stockMovement.getBarcode());
            stmt.setString(4, stockMovement.getInvoiceNumber());
            stmt.setString(5, stockMovement.getUser());
            stmt.setString(6, stockMovement.getDate());

            stmt.executeUpdate();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while inserting into stock movement table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //INVOICE_NUMBER sütununu sürekli artıran metod
    public String getLatestInvoiceNumber() {
        String invoiceNumber;
        String query = "SELECT \"INVOICE_NUMBER\" FROM \"INVOICES\" ORDER BY \"INVOICE_NUMBER\" DESC LIMIT 1";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                invoiceNumber = rs.getString("INVOICE_NUMBER");
                return String.format("%06d", Integer.parseInt(invoiceNumber) + 1);

            } else {
                return "000001";
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting latest invoice number.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return "000001";
        }
    }

    //PRODUCT tablosunda QUANTITY değerini azaltan metod
    public void reduceStockQuantity(Product product) {

        for (int p = 0; p < product.getQuantity(); p++) {
            int stockQuantity = Integer.parseInt(getInventoryCellByBarcode("QUANTITY", product.getBarcode()));
            String query = "UPDATE \"INVENTORY\" SET \"QUANTITY\" = " + (stockQuantity - 1) + " WHERE \"BARCODE\" = ?";
            try (Connection conn = DatabasePool.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, product.getBarcode());

                stmt.executeUpdate();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("An error occured while reducing stock quantity.");
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
        }

    }

    //CARD tablosundan bilgi getiren metod
    public String getCardInfo(String column1, String column2, String cell) {
        String returnCell = null;
        String query = "SELECT \"" + column1 + "\" FROM \"CARDS\" WHERE \"" + column2 + "\" = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cell);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                returnCell = rs.getString(column1);
                return returnCell;
            }
            return null;

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting card info.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return returnCell;
        }
    }

    //Kart bakiyesini düşüren metod
    public void reduceCardBalance(Double paidAmount, String cardNumber) {
        double firstAmount = Double.parseDouble(getCardInfo("BALANCE", "NUMBER", cardNumber));
        double lastAmount = firstAmount - paidAmount;

        String query = "UPDATE \"CARDS\" SET \"BALANCE\" = ? WHERE \"NUMBER\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Double.toString(lastAmount));
            stmt.setString(2, cardNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while reducing card balance.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //INVOICE tablosunda yeni fiş kaydı oluşturan metod
    public void insertIntoInvoicesTable(Invoice invoice) {


        String query = "INSERT INTO \"INVOICES\" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, invoice.getInvoiceNumber());
            stmt.setString(2, invoice.getPaymentType());
            stmt.setString(3, invoice.getCardNumber());
            stmt.setString(4, invoice.getPaidAmount());
            stmt.setString(5, invoice.getDiscountAmount());
            stmt.setString(6, invoice.getActualCartAmount());
            stmt.setString(7, invoice.getCashierId());
            stmt.setString(8, invoice.getDate());
            stmt.setString(9, invoice.getOriginalInvoiceNumber());

            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while inserting into invoices table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //CAMPAIGN tablosuna yeni kampanya ekleme metodu
    public void insertIntoCampaignTable(Campaign campaign) {

        String query = "INSERT INTO \"CAMPAIGN\" VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, campaign.getCampaignId());
            stmt.setString(2, campaign.getDiscountType());
            stmt.setString(3, campaign.getDiscountTypeCode());
            stmt.setString(4, campaign.getDiscountFor());
            stmt.setString(5, campaign.getStartDate());
            stmt.setString(6, campaign.getEndDate());
            stmt.setString(7, campaign.getIsActive());

            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while inserting into campaign table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //CAMPAIGN_ID sürekli artıran metod
    public String getLatestCampaignId() {
        String campaignId;
        String query = "SELECT \"CAMPAIGN_ID\" FROM \"CAMPAIGN\" ORDER BY \"CAMPAIGN_ID\" DESC LIMIT 1";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaignId = rs.getString("CAMPAIGN_ID");
                return String.format("%03d", Integer.parseInt(campaignId) + 1);

            } else {
                return "001";
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting latest campaign id.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return "001";
        }
    }

    //CAMPAIGN tablosundan Campaign modeli veren metod
    public Campaign getCampaignModelById(String id) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"CAMPAIGN_ID\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(
                        rs.getString("CAMPAIGN_ID"),
                        rs.getString("DISCOUNT_TYPE"),
                        rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting campaign model.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return campaign;


    }

    //CAMPAIGN tablosundaki her satırı List olarak veren metod
    public List<Campaign> getCampaignList() {
        String query = "SELECT * FROM \"CAMPAIGN\"";
        List<Campaign> campaignList = new ArrayList<>();
        Campaign campaign;

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                campaign = new Campaign(
                        rs.getString("CAMPAIGN_ID"),
                        rs.getString("DISCOUNT_TYPE"),
                        rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"));
                campaignList.add(campaign);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting campaign list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return campaignList;
    }

    //CAMPAIGN tablosundaki bir satırı değiştiren metod
    public void updateCampaignTable(Campaign campaign) {
        String query = "UPDATE \"CAMPAIGN\" SET " + "\"CAMPAIGN_ID\" = ?, " + "\"DISCOUNT_TYPE\" = ?, " +
                "\"DISCOUNT_TYPE_CODE\" = ?, " + "\"DISCOUNT_FOR\" = ?, " + "\"START_DATE\" = ?, " +
                "\"END_DATE\" = ?, " + "\"IS_ACTIVE\" = ? " + "WHERE \"CAMPAIGN_ID\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while updating campaign table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //CAMPAIGN tablosundan kayıt silme metodu
    public void deleteFromCampaignById(String campaignId) {
        String query = "DELETE FROM \"CAMPAIGN\" WHERE \"CAMPAIGN_ID\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, campaignId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while deleting campaign.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //Aktif olan 2 al 1 öde kampanyasını getiren metod
    public Campaign getBuy2Get1CampaignByBarcode(String barcode) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ? AND" +
                " \"DISCOUNT_TYPE_CODE\" = '01' AND \"IS_ACTIVE\" = 'ACTIVE' AND " +
                "CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')"
                + " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(
                        rs.getString("CAMPAIGN_ID"),
                        rs.getString("DISCOUNT_TYPE"),
                        rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting campaign.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return campaign;
    }

    //Aktif olan kategori bazlı 2. ürüne %50 kampanyasını getiren metod
    public Campaign get50CampaignForCategory(String category) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ? AND " +
                "\"DISCOUNT_TYPE_CODE\" = '03' AND \"IS_ACTIVE\" = 'ACTIVE' AND " +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(rs.getString(
                        "CAMPAIGN_ID"),
                        rs.getString("DISCOUNT_TYPE"),
                        rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting campaign.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return campaign;
    }

    //Aktif olan ürün bazlı 2. ürüne %50 kampanyasını getiren metod
    public Campaign get50CampaignForProduct(String barcode) {
        Campaign campaign = null;

        String query = "SELECT * FROM \"CAMPAIGN\" WHERE \"DISCOUNT_FOR\" = ? AND" +
                " \"DISCOUNT_TYPE_CODE\" = '02' AND \"IS_ACTIVE\" = 'ACTIVE' AND " +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                campaign = new Campaign(
                        rs.getString("CAMPAIGN_ID"),
                        rs.getString("DISCOUNT_TYPE"),
                        rs.getString("DISCOUNT_TYPE_CODE"),
                        rs.getString("DISCOUNT_FOR"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"));
                return campaign;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting campaign.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return campaign;
    }

    //Aktif olan kupon indirimini getiren metod
    public Coupon getCouponModelByCode(String code) {
        Coupon coupon = null;

        String query = "SELECT * FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                coupon = new Coupon(
                        rs.getString("COUPON_CODE"),
                        rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"),
                        rs.getString("USED"),
                        rs.getString("USING_LIMIT"));
                return coupon;
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting employee model.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return coupon;
    }

    //Tüm kuponları liste halinde getiren metod
    public List<Coupon> getCouponList() {
        String query = "SELECT * FROM \"COUPON\"";
        List<Coupon> couponList = new ArrayList<>();
        Coupon coupon;

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                coupon = new Coupon(
                        rs.getString("COUPON_CODE"),
                        rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"),
                        rs.getString("USED"),
                        rs.getString("USING_LIMIT"));
                couponList.add(coupon);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting coupon list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return couponList;
    }

    //COUPON tablosudaki bir satırı değiştiren metod
    public void updateCouponTable(Coupon coupon) {
        String query = "UPDATE \"COUPON\" SET " + "\"COUPON_CODE\" = ?, " +
                "\"DISCOUNT_AMOUNT\" = ?, " + "\"START_DATE\" = ?, " +
                "\"END_DATE\" = ?, " + "\"IS_ACTIVE\" = ? " + "WHERE \"COUPON_CODE\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, coupon.getCouponCode());
            stmt.setString(2, coupon.getDiscountAmount());
            stmt.setString(3, coupon.getStartDate());
            stmt.setString(4, coupon.getEndDate());
            stmt.setString(5, coupon.getIsActive());
            stmt.setString(6, coupon.getCouponCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while updating coupon talbe.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //COUPON tablosundaki bir satırı silen metod
    public void deleteFromCouponbyCode(String couponCode) {
        String query = "DELETE FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, couponCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while deleting coupon.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //COUPON tablosuna bir satır ekleme metodu
    public void insertIntoCouponTable(Coupon coupon) {
        String query = "INSERT INTO \"COUPON\" VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, coupon.getCouponCode());
            stmt.setString(2, coupon.getDiscountAmount());
            stmt.setString(3, coupon.getStartDate());
            stmt.setString(4, coupon.getEndDate());
            stmt.setString(5, coupon.getIsActive());
            stmt.setString(6, "0");
            stmt.setString(7, coupon.getUsingLimit());

            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while inserting into coupon table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //COUPON_CODE sürekli artıran metod
    public String getLatestCouponCode() {
        String couponCode;
        String query = "SELECT \"COUPON_CODE\" FROM \"COUPON\" ORDER BY \"COUPON_CODE\" DESC LIMIT 1";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                couponCode = rs.getString("COUPON_CODE");
                return String.format("%03d", Integer.parseInt(couponCode) + 1);

            } else {
                return "001";
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting latest coupon code.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return "001";
        }
    }

    //Kupon kodundan geçerli olan kuponu getiren metod
    public Coupon getValidCoupon(String couponCode) {
        Coupon coupon = null;
        String query = "SELECT * FROM \"COUPON\" " +
                "WHERE \"COUPON_CODE\" = ? AND \"IS_ACTIVE\" = 'ACTIVE' AND" +
                " CURRENT_DATE BETWEEN TO_DATE(\"START_DATE\", 'MM-DD-YYYY')" +
                " AND TO_DATE(\"END_DATE\", 'MM-DD-YYYY')" +
                " AND CAST(\"USED\" AS INT) < CAST(\"USING_LIMIT\" AS INT)";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, couponCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                coupon = new Coupon(rs.getString(
                        "COUPON_CODE"),
                        rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("IS_ACTIVE"),
                        rs.getString("USED"),
                        rs.getString("USING_LIMIT"));
                return coupon;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting valid coupon.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return coupon;

    }

    //COUPON tablosundaki USED sütununu artırmaya yarayan metod
    public void updateCouponUsed(String couponCode) {
        String _query = "SELECT \"USED\" FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(_query)) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while updating amount of coupon use.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //COUPON tablosundaki USED sütununun değerini getiren metod
    public String getCouponUsed(String couponCode) {
        String used = "";
        String query = "SELECT \"USED\" FROM \"COUPON\" WHERE \"COUPON_CODE\" = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, couponCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                used = rs.getString("USED");
                return used;
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting amount of coupon use.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return used;
    }

    public String getLatestMovementId() {
        String movementId;
        String query = "SELECT \"MOVEMENT_ID\" FROM \"STOCK_MOVEMENT\" ORDER BY \"MOVEMENT_ID\" DESC LIMIT 1";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                movementId = rs.getString("MOVEMENT_ID");
                return String.format("%06d", Integer.parseInt(movementId) + 1);

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

    public List<Product> getProductListByInvoiceNumber(String invoiceNumber) {
        String query = "SELECT \"BARCODE\" FROM \"STOCK_MOVEMENT\" WHERE \"INVOICE_NUMBER\" = ? ";
        String barcode;
        List<Product> productList = new ArrayList<>();
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, invoiceNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                barcode = rs.getString("BARCODE");
                Product product = getProductModelByBarcode(barcode);
                product.setQuantity(1);
                productList.add(product);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting product list by invoice number.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return productList;
    }

    public Invoice getInvoiceModelByInvoiceNumber(String invoiceNumber) {
        Invoice invoice = null;
        String query = "SELECT * FROM \"INVOICES\" WHERE \"INVOICE_NUMBER\" = ? ";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, invoiceNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                invoice = new Invoice(
                        rs.getString("INVOICE_NUMBER"),
                        rs.getString("PAYMENT_TYPE"),
                        rs.getString("CARD_NUMBER"),
                        rs.getString("PAID_AMOUNT"),
                        rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("ACTUAL_CART_AMOUNT"),
                        rs.getString("CASHIER_ID"),
                        rs.getString("DATE"),
                        rs.getString("ORIGINAL_INVOICE_NUMBER"));
                return invoice;
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting invoice model.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return invoice;
    }

    public List<Invoice> getInvoiceList() {
        Invoice invoice;
        List<Invoice> invoiceList = new ArrayList<>();
        String query = "SELECT * FROM \"INVOICES\"";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                invoice = new Invoice(rs.getString("INVOICE_NUMBER"),
                        rs.getString("PAYMENT_TYPE"),
                        rs.getString("CARD_NUMBER"),
                        rs.getString("PAID_AMOUNT"),
                        rs.getString("DISCOUNT_AMOUNT"),
                        rs.getString("ACTUAL_CART_AMOUNT"),
                        rs.getString("CASHIER_ID"),
                        rs.getString("DATE"),
                        rs.getString("ORIGINAL_INVOICE_NUMBER"));
                invoiceList.add(invoice);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting invoice list.");
            alert.setContentText(e.toString());
            alert.showAndWait();        }
        return invoiceList;
    }


}
