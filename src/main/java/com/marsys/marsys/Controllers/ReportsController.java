package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.*;
import com.marsys.marsys.Models.*;

import com.marsys.marsys.Repository.OmerRepo;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import javafx.geometry.Orientation;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


public class ReportsController implements Initializable {
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();
    OmerRepo omerRepo = new OmerRepo();


    @FXML
    private Button btnBack;
    @FXML
    public Label lblUserId;
    @FXML
    public Label lblUserName;


    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TitledPane topSellingPane;
    @FXML
    private TableView<TopProducts> topProductsTable;
    @FXML
    private TableColumn<TopProducts, String> colProductName;
    @FXML
    private TableColumn<TopProducts, Integer> colQuantity;
    @FXML
    private TableColumn<TopProducts, Double> colTotalSales;

    @FXML
    private TableView<TopProducts> topCategoriesTable;
    @FXML
    private TableColumn<TopProducts, String> colCategoryName;
    @FXML
    private TableColumn<TopProducts, Integer> colCategoryQuantity;
    @FXML
    private TableColumn<TopProducts, Double> colCategorySales;

    @FXML
    private TableView<DailyPaymentSummary> paymentSummaryTable;
    @FXML
    private TableColumn<DailyPaymentSummary, LocalDate> colPaymentDate;
    @FXML
    private TableColumn<DailyPaymentSummary, Double> colCashAmount;
    @FXML
    private TableColumn<DailyPaymentSummary, Double> colCardAmount;

    @FXML
    private LineChart<String, Double> dailyRevenueChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private TableView<DailyInvoiceReport> profitMarginTable;
    @FXML
    private TableColumn<DailyInvoiceReport, LocalDate> colPeriod;
    @FXML
    private TableColumn<DailyInvoiceReport, Double> colRevenue;
    @FXML
    private TableColumn<DailyInvoiceReport, Double> colCost;
    @FXML
    private TableColumn<DailyInvoiceReport, Double> colProfit;
    @FXML
    private TableColumn<DailyInvoiceReport, Double> colMargin;

    @FXML
    private TableView<TopStaffs> topStaffTable;
    @FXML
    private TableColumn<TopStaffs, String> colStaffName;
    @FXML
    private TableColumn<TopStaffs, Double> colStaffSales;
    @FXML
    private TableColumn<TopStaffs, Integer> colStaffTransactions;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());


        colProductName.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.35));
        colQuantity.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.35));
        colTotalSales.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.27));

        colCategoryName.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.35));
        colCategoryQuantity.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.35));
        colCategorySales.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.27));

        colCardAmount.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.35));
        colCashAmount.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.35));
        colPaymentDate.prefWidthProperty().bind(topProductsTable.widthProperty().multiply(0.27));

        colPeriod.prefWidthProperty().bind(profitMarginTable.widthProperty().multiply(0.17));
        colRevenue.prefWidthProperty().bind(profitMarginTable.widthProperty().multiply(0.2));
        colCost.prefWidthProperty().bind(profitMarginTable.widthProperty().multiply(0.2));
        colProfit.prefWidthProperty().bind(profitMarginTable.widthProperty().multiply(0.2));
        colMargin.prefWidthProperty().bind(profitMarginTable.widthProperty().multiply(0.2));

        colStaffName.prefWidthProperty().bind(topStaffTable.widthProperty().multiply(0.27));
        colStaffSales.prefWidthProperty().bind(topStaffTable.widthProperty().multiply(0.35));
        colStaffTransactions.prefWidthProperty().bind(topStaffTable.widthProperty().multiply(0.35));

        initializeTopProductsTable();
        initializeTopCategoriesTable();
        initializePaymentSummaryTable();
        initializeDailyRevenueChart();
        initializeProfitMarginTable();
        initializeTopStaffTable();

    }


    private void initializeTopProductsTable() {
        ObservableList<TopProducts> topProductsList = FXCollections.observableArrayList();
        topProductsList.addAll(omerRepo.getTop20ProductSoldLastWeek());

        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colTotalSales.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.get2DecimalDouble(cellData.getValue().getTotalSales())).asObject());

        topProductsTable.setItems(topProductsList);

    }

    private void initializeTopCategoriesTable() {
        ObservableList<TopProducts> topCategoryList = FXCollections.observableArrayList();
        topCategoryList.addAll(omerRepo.getOrderedCategorySoldLastWeek());

        colCategoryName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        colCategoryQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colCategorySales.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.get2DecimalDouble(cellData.getValue().getTotalSales())).asObject());

        topCategoriesTable.setItems(topCategoryList);
    }

    private void initializePaymentSummaryTable() {
        ObservableList<DailyPaymentSummary> dailyPaymentList = FXCollections.observableArrayList();

        dailyPaymentList.addAll(omerRepo.getDailyPaymentTypeList());

        colPaymentDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(
                        ProgramHelpers.getLocalDateByStringDate(cellData.getValue().getSaleDate())
                )
        );

        colPaymentDate.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(ProgramHelpers.getStringDateByLocalDate(item));
                        }
                    }
                });
        colCardAmount.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.
                        get2DecimalDouble(cellData.getValue().getCardAmount())).asObject());
        colCashAmount.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.
                        get2DecimalDouble(cellData.getValue().getCashAmount())).asObject());

        paymentSummaryTable.setItems(dailyPaymentList);
    }

    private void initializeDailyRevenueChart() {
        List<DailyRevenue> list = omerRepo.getDailyRevenueForLastMonth();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        ObservableList<String> categories = FXCollections.observableArrayList();
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Daily Revenue");

        double maxRevenue = 0;

        for (DailyRevenue revenue : list) {
            double value = ProgramHelpers.get2DecimalDouble(revenue.getRevenue());

            LocalDate date = LocalDate.parse(revenue.getDate(), inputFormatter);
            String formattedDate = date.format(outputFormatter);
            categories.add(formattedDate);

            XYChart.Data<String, Double> data = new XYChart.Data<>(formattedDate, value);

            // Tooltip ekleme (node oluşunca tetiklenir)
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip tooltip = new Tooltip(
                            "Date: " + data.getXValue() + "\n" +
                                    "Revenue: " + String.format("%.2f", data.getYValue())
                    );
                    tooltip.setShowDelay(Duration.millis(100));
                    tooltip.setHideDelay(Duration.millis(100));
                    newNode.setMouseTransparent(false);
                    Tooltip.install(newNode, tooltip);
                }
            });

            series.getData().add(data);

            if (value > maxRevenue) {
                maxRevenue = value;
            }
        }

        xAxis.setCategories(categories);
        dailyRevenueChart.getData().clear();
        dailyRevenueChart.getData().add(series);
        dailyRevenueChart.setCreateSymbols(true);

        xAxis.setTickLabelGap(10);
        xAxis.setAnimated(false);

        double padding = maxRevenue * 0.1;
        double upperBound = maxRevenue + padding;
        double tickUnit = padding / 2;

        Platform.runLater(() -> {
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(ProgramHelpers.get2DecimalDouble(upperBound));
            yAxis.setTickUnit(ProgramHelpers.get2DecimalDouble(tickUnit));

            xAxis.setTickLabelRotation(45);

            // Y ekseni başlığını sola kaydır (çakışmayı önler)
            Node yAxisLabel = yAxis.lookup(".axis-label");
            if (yAxisLabel != null) {
                yAxisLabel.setStyle("-fx-translate-x: -20;");
            }
        });
    }


    private void initializeProfitMarginTable() {
        ObservableList<DailyInvoiceReport> dailyInvoiceReportList = FXCollections.observableArrayList();

        dailyInvoiceReportList.addAll(omerRepo.getDailyInvoiceReport());

        colPeriod.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(
                        ProgramHelpers.getLocalDateByStringDate(cellData.getValue().getDate())
                )
        );

        colPeriod.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(ProgramHelpers.getStringDateByLocalDate(item));
                }
            }
        });
        colRevenue.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getRevenue()).asObject());
        colCost.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.
                        get2DecimalDouble(cellData.getValue().getCost())).asObject());
        colProfit.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProfit()).asObject());
        colMargin.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getMargin()).asObject()
        );

        colMargin.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double margin, boolean empty) {
                super.updateItem(margin, empty);
                if (empty || margin == null) {
                    setText(null);
                    setStyle("");
                } else {

                    setText(ProgramHelpers.getPercentageDisplay(margin));

                    if (margin < 0) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else if (margin < 0.2) {
                        setStyle("-fx-background-color: #fff9cc;");
                    } else {
                        setStyle("-fx-background-color: #ccffcc;");
                    }
                }
            }
        });

        profitMarginTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DailyInvoiceReport item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    double margin = item.getMargin();

                    if (margin < 0) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else if (margin < 0.2) {
                        setStyle("-fx-background-color: #fff9cc;");
                    } else {
                        setStyle("-fx-background-color: #ccffcc;");
                    }
                }
            }
        });

        profitMarginTable.setItems(dailyInvoiceReportList);
    }

    private void initializeTopStaffTable() {
        ObservableList<TopStaffs> topStaffList = FXCollections.observableArrayList();

        topStaffList.addAll(omerRepo.getTopStaffs());


        colStaffName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffName()));
        colStaffSales.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.
                        get2DecimalDouble(cellData.getValue().getStaffSales())).asObject());
        colStaffTransactions.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStaffTransactions()).asObject());

        topStaffTable.setItems(topStaffList);
    }



    @FXML
    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }
}
