package com.marsys.marsys.Repository;

import com.marsys.marsys.Helpers.*;
import com.marsys.marsys.Models.Invoice;
import com.marsys.marsys.Models.StockMovement;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OmerRepo {
    Repository repository = new Repository();

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

    public List<Invoice> getInvoiceListForLastMonth() {
        List<Invoice> invoiceList = new ArrayList<>();

        String query = "SELECT * FROM \"INVOICES\" WHERE " +
                "TO_DATE(\"DATE\", 'MM-DD-YYYY') BETWEEN TO_DATE(?, 'MM-DD-YYYY') " +
                "AND TO_DATE(?, 'MM-DD-YYYY') " +
                "ORDER BY TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') DESC";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ProgramHelpers.
                    getStringDateByLocalDate(LocalDate.now().minusMonths(1)));
            stmt.setString(2, ProgramHelpers.getStringDateByLocalDate(LocalDate.now()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice(rs.getString("INVOICE_NUMBER"),
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
            invoiceList.sort(Comparator.comparing(Invoice::getPaidAmount).reversed());

            return invoiceList;

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting invoice list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return invoiceList;
    }

    public List<TopProducts> getTop20ProductSoldLastWeek() {
        List<TopProducts> topProductsList = new ArrayList<>();

        String query = "SELECT " +
                "        \"BARCODE\", " +
                "        COUNT(*) AS repeat_count," +
                "        array_agg(\"INVOICE_NUMBER\") AS invoices " +
                "    FROM \"STOCK_MOVEMENT\" " +
                "    WHERE TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') >= NOW() - INTERVAL '7 days' " +
                "      AND \"MOVEMENT_TYPE\" = 'SALE' " +
                "    GROUP BY \"BARCODE\" " +
                "    ORDER BY repeat_count DESC " +
                "    LIMIT 20;";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String barcode = rs.getString("BARCODE");
                int repeatCount = rs.getInt("repeat_count");
                Array invoiceArray = rs.getArray("invoices");
                String[] invoiceNumbers = (String[]) invoiceArray.getArray();

                double totalPaid = 0.0;

                for (String invoiceNumber : invoiceNumbers) {
                    Invoice invoice = repository.getInvoiceModelByInvoiceNumber(invoiceNumber);
                    double rate = Double.parseDouble(invoice.getPaidAmount()) / Double.parseDouble(invoice.getActualCartAmount());
                    double price = repository.getProductModelByBarcode(barcode).getPrice();

                    totalPaid += rate * price;
                }

                String productName = repository.getProductModelByBarcode(barcode).getProductName();

                TopProducts topProduct = new TopProducts(productName, repeatCount, totalPaid);
                topProductsList.add(topProduct);
            }
            topProductsList.sort(Comparator.comparing(TopProducts::getTotalSales).reversed());
            return topProductsList;

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting top sales.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }


        return topProductsList;

    }

    public List<TopProducts> getOrderedCategorySoldLastWeek() {
        List<TopProducts> topCategorySalesList = new ArrayList<>();
        String query = "SELECT " +
                "    i.\"CATEGORY\", " +
                "    sm.\"BARCODE\", " +
                "    COUNT(*) AS repeat_count, " +
                "    array_agg(sm.\"INVOICE_NUMBER\") AS invoices " +
                "FROM \"STOCK_MOVEMENT\" sm " +
                "JOIN \"INVENTORY\" i ON sm.\"BARCODE\" = i.\"BARCODE\" " +
                "WHERE TO_TIMESTAMP(sm.\"DATE\", 'MM-DD-YYYY HH24:MI:SS') >= NOW() - INTERVAL '7 days' " +
                "  AND sm.\"MOVEMENT_TYPE\" = 'SALE' " +
                "GROUP BY i.\"CATEGORY\", sm.\"BARCODE\" " +
                "ORDER BY i.\"CATEGORY\", repeat_count DESC";

        Map<String, TopProducts> categoryMap = new HashMap<>();

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString("CATEGORY");
                String barcode = rs.getString("BARCODE");
                int repeatCount = rs.getInt("repeat_count");
                Array invoiceArray = rs.getArray("invoices");
                String[] invoiceNumbers = (String[]) invoiceArray.getArray();

                double totalPaid = 0.0;

                for (String invoiceNumber : invoiceNumbers) {
                    Invoice invoice = repository.getInvoiceModelByInvoiceNumber(invoiceNumber);
                    double rate = Double.parseDouble(invoice.getPaidAmount()) / Double.parseDouble(invoice.getActualCartAmount());
                    double price = repository.getProductModelByBarcode(barcode).getPrice();

                    totalPaid += rate * price;
                }

                if (categoryMap.containsKey(category)) {
                    TopProducts current = categoryMap.get(category);
                    current.setQuantity(current.getQuantity() + repeatCount);
                    current.setTotalSales(current.getTotalSales() + totalPaid);
                } else {
                    categoryMap.put(category, new TopProducts(category, repeatCount, totalPaid));
                }
            }

            topCategorySalesList.addAll(categoryMap.values());
            topCategorySalesList = categoryMap.values().stream()
                    .sorted(Comparator.comparing(TopProducts::getTotalSales).reversed())
                    .collect(Collectors.toList());
            return topCategorySalesList;

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occurred while getting top category sales.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }

        return topCategorySalesList;
    }

    public List<DailyPaymentSummary> getDailyPaymentTypeList() {
        DailyPaymentSummary dailyPaymentSummary;
        List<DailyPaymentSummary> dailyPaymentSummaryList = new ArrayList<>();
        String query = "SELECT " +
                "  TO_CHAR(DATE(TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS')), 'MM-DD-YYYY') AS sale_date, " +
                "  SUM(CASE WHEN \"PAYMENT_TYPE\" = 'CARD' THEN \"PAID_AMOUNT\"::numeric ELSE 0 END) AS card_amount, " +
                "  SUM(CASE WHEN \"PAYMENT_TYPE\" = 'CASH' THEN \"PAID_AMOUNT\"::numeric ELSE 0 END) AS cash_amount " +
                "FROM \"INVOICES\" " +
                "WHERE TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS') >= CURRENT_DATE - INTERVAL '30 days' " +
                "GROUP BY DATE(TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS')) " +
                "ORDER BY DATE(TO_TIMESTAMP(\"DATE\", 'MM-DD-YYYY HH24:MI:SS'));";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dailyPaymentSummary = new DailyPaymentSummary(rs.getString("sale_date"),
                        rs.getDouble("card_amount"), rs.getDouble("cash_amount"));
                dailyPaymentSummaryList.add(dailyPaymentSummary);
            }
            return dailyPaymentSummaryList;

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occurred while getting payment type list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return dailyPaymentSummaryList;

    }

    public List<DailyRevenue> getDailyRevenueForLastMonth() {
        String query = "WITH date_series AS (" +
                " SELECT generate_series(CURRENT_DATE - INTERVAL '1 month', CURRENT_DATE, INTERVAL '1 day')::date AS SaleDate" +
                ") " +
                "SELECT ds.SaleDate, " +
                "       COALESCE(SUM(CAST(i.\"PAID_AMOUNT\" AS NUMERIC)), 0) AS TotalRevenue " +
                "FROM date_series ds " +
                "LEFT JOIN \"INVOICES\" i ON TO_DATE(i.\"DATE\", 'MM-DD-YYYY') = ds.SaleDate " +
                "       AND (i.\"ORIGINAL_INVOICE_NUMBER\" IS NULL OR i.\"ORIGINAL_INVOICE_NUMBER\" = '') " +
                "GROUP BY ds.SaleDate " +
                "ORDER BY ds.SaleDate ASC;";

        List<DailyRevenue> list = new ArrayList<>();
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("SaleDate");
                double revenue = rs.getDouble("TotalRevenue");
                list.add(new DailyRevenue(date, revenue));
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occurred while getting daily revenue for last month.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }

        return list;
    }


    public List<DailyInvoiceReport> getDailyInvoiceReport() {
        DailyInvoiceReport dailyInvoiceReport;
        List<DailyInvoiceReport> dailyInvoiceReportList = new ArrayList<>();
        double revenue = 0;
        double cost = 0;
        Invoice invoice;
        List<Invoice> invoiceList = new ArrayList<>();
        String query = "SELECT * FROM \"INVOICES\" WHERE \"ORIGINAL_INVOICE_NUMBER\" IS NULL AND " +
                " TO_DATE(\"DATE\", 'MM-DD-YYYY') >= CURRENT_DATE - INTERVAL '1 MONTH';";

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

            for (Invoice i : invoiceList) {
                double rate = ProgramHelpers.get2DecimalDoubleFromString(i.getPaidAmount()) /
                        ProgramHelpers.get2DecimalDoubleFromString(i.getActualCartAmount());
                List<StockMovement> stockMovementList = new ArrayList<>();
                String _query = "SELECT * FROM \"STOCK_MOVEMENT\" WHERE \"INVOICE_NUMBER\" = ?;";
                try (PreparedStatement _stmt = conn.prepareStatement(_query)) {
                    _stmt.setString(1, i.getInvoiceNumber());
                    ResultSet _rs = _stmt.executeQuery();
                    while (_rs.next()) {
                        StockMovement stockMovement = new StockMovement(
                                _rs.getString("MOVEMENT_ID"),
                                _rs.getString("MOVEMENT_TYPE"),
                                _rs.getString("BARCODE"),
                                _rs.getString("INVOICE_NUMBER"),
                                _rs.getString("USER"),
                                _rs.getString("DATE"));
                        stockMovementList.add(stockMovement);

                    }
                    for (StockMovement sm : stockMovementList) {
                        if (sm.getMovementType().equals("RETURN")) {
                            revenue -= ProgramHelpers.get2DecimalDouble(
                                    rate * repository.getProductModelByBarcode(sm.getBarcode()).getPrice());
                            cost -= repository.getProductModelByBarcode(sm.getBarcode()).getBuyingPrice();

                        }
                        if (sm.getMovementType().equals("SALE")) {
                            revenue += ProgramHelpers.get2DecimalDouble(
                                    rate * repository.getProductModelByBarcode(sm.getBarcode()).getPrice());
                            cost += repository.getProductModelByBarcode(sm.getBarcode()).getBuyingPrice();

                        }

                    }
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Database Error");
                    alert.setHeaderText("An error occured while getting daily invoice report.");
                    alert.setContentText(e.toString());
                    alert.showAndWait();

                }
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");

                LocalDateTime dateTime = LocalDateTime.parse(i.getDate(), inputFormat);
                i.setDate(outputFormat.format(dateTime.toLocalDate()));

                double profit = revenue - cost;
                double margin = cost != 0 ? profit / cost : 0.00;
                dailyInvoiceReport = new DailyInvoiceReport(i.getDate(), revenue, cost, profit, margin);
                boolean found = false;
                for (DailyInvoiceReport dir : dailyInvoiceReportList) {
                    if ((dailyInvoiceReport.getDate().equals(dir.getDate()))) {
                        dir.setRevenue(ProgramHelpers.get2DecimalDouble(dailyInvoiceReport.getRevenue() + dir.getRevenue()));
                        dir.setCost(ProgramHelpers.get2DecimalDouble(dailyInvoiceReport.getCost() + dir.getCost()));
                        dir.setProfit(ProgramHelpers.get2DecimalDouble(dailyInvoiceReport.getProfit() + dir.getProfit()));
                        double newRevenue = dir.getRevenue();
                        double newProfit = dir.getProfit();

                        if (newRevenue != 0) {
                            dir.setMargin(ProgramHelpers.get2DecimalDouble(newProfit / newRevenue));
                        } else {
                            dir.setMargin(0);
                        }
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    dailyInvoiceReportList.add(dailyInvoiceReport);
                }


                revenue = 0;
                cost = 0;
            }
            dailyInvoiceReportList.sort((o1, o2) -> {
                LocalDate date1 = ProgramHelpers.getLocalDateByStringDate(o1.getDate());
                LocalDate date2 = ProgramHelpers.getLocalDateByStringDate(o2.getDate());
                return date1.compareTo(date2);
            });


        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting daily invoice report.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return dailyInvoiceReportList;
    }

    public List<TopStaffs> getTopStaffs() {
        TopStaffs topStaffs;
        int staffTransactions = 0;
        List<TopStaffs> topStaffList = new ArrayList<>();
        List<Invoice> invoiceList = repository.getInvoiceList();
        double totalSales = 0.00;
        for (Invoice i : invoiceList) {
            totalSales += ProgramHelpers.get2DecimalDoubleFromString(i.getPaidAmount());
            topStaffs = new TopStaffs(repository.getEmployeeModelById(i.getCashierId()).getFirstName() +
                    " " + repository.getEmployeeModelById(i.getCashierId()).getLastName(),
                    ProgramHelpers.get2DecimalDouble(totalSales),
                    staffTransactions + 1);
            boolean found = false;
            for (TopStaffs ts : topStaffList) {
                if (topStaffs.getStaffName().equals(ts.getStaffName())) {
                    ts.setStaffSales(ProgramHelpers.get2DecimalDouble(totalSales) +
                            ProgramHelpers.get2DecimalDouble(ts.getStaffSales()));
                    ts.setStaffTransactions(ts.getStaffTransactions() + 1);
                    found = true;
                    break;
                }

            }
            if (!found) {
                topStaffList.add(topStaffs);

            }
            totalSales = 0.00;

        }
        topStaffList.sort(Comparator.comparingDouble(TopStaffs::getStaffSales).reversed());

        return topStaffList;
    }
}