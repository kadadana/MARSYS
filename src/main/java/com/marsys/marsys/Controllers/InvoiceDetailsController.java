package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.ProgramHelpers;
import com.marsys.marsys.Models.Invoice;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.StockMovement;
import com.marsys.marsys.Repository.BeratRepo;
import com.marsys.marsys.Repository.Repository;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailsController {
    @FXML
    private Label lblInvoiceNumber;
    @FXML
    private Label lblCashier;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblDiscountTotal;
    @FXML
    private Label lblLastTotal;
    @FXML
    private Label lblRefundedTotal;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private Label lblInvoiceDate;
    @FXML
    private TableColumn<Product, String> colBarcode;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, String> colCategory;
    @FXML
    private TableColumn<Product, Integer> colQuantity;
    @FXML
    private TableColumn<Product, String> colBrand;
    @FXML
    private TableColumn<Product, Double> colPaid;
    @FXML
    private TableColumn<Product, Double> colDiscount;
    @FXML
    private TableColumn<Product, String> colExpiration;
    @FXML
    private TableColumn<Product, String> colMovementType;
    @FXML
    private TableColumn<Product, Double> colPrice;

    Repository repository = new Repository();
    BeratRepo beratRepo = new BeratRepo();
    RepositoryMete repositoryMete = new RepositoryMete();
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    public void setInvoice(Invoice invoice) {

        double paidRate = ProgramHelpers.get2DecimalDouble(Double.parseDouble(invoice.getPaidAmount()) /
                Double.parseDouble(invoice.getActualCartAmount()));
        double refundedTotal = 0.00;

        List<StockMovement> stockMovementList = repositoryMete.getStockMovementListBySearching(
                "07",
                null,
                null,
                null,
                invoice.getInvoiceNumber());
        List<Product> productList = new ArrayList<>();
        for (StockMovement sm : stockMovementList) {
            Product product = repository.getProductModelByBarcode(sm.getBarcode());
            product.setMovementType(sm.getMovementType());
            product.setDiscountedPrice(ProgramHelpers.get2DecimalDouble(product.getPrice() * paidRate));
            productList.add(product);
            if (product.getMovementType().equals("RETURN")) {
                refundedTotal += product.getDiscountedPrice();
            }

        }


        lblInvoiceNumber.setText(invoice.getInvoiceNumber());
        lblTotal.setText(invoice.getActualCartAmount());
        lblDiscountTotal.setText(invoice.getDiscountAmount());
        lblLastTotal.setText(invoice.getPaidAmount());
        lblRefundedTotal.setText(ProgramHelpers.get2DecimalString(refundedTotal));
        products.addAll(productList);
        lblInvoiceDate.setText(invoice.getDate());
        lblCashier.setText(invoice.getCashierId() + " - " +
                beratRepo.getEmployeeModelById(invoice.getCashierId()).getFirstName() + " " +
                beratRepo.getEmployeeModelById(invoice.getCashierId()).getLastName());

        colBarcode.prefWidthProperty().bind(productTable.widthProperty().multiply(0.07));
        colProductName.prefWidthProperty().bind(productTable.widthProperty().multiply(0.13));
        colQuantity.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));
        colCategory.prefWidthProperty().bind(productTable.widthProperty().multiply(0.13));
        colPaid.prefWidthProperty().bind(productTable.widthProperty().multiply(0.10));
        colBrand.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));
        colDiscount.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));
        colPrice.prefWidthProperty().bind(productTable.widthProperty().multiply(0.10));
        colExpiration.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));
        colMovementType.prefWidthProperty().bind(productTable.widthProperty().multiply(0.12));

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));

        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));

        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        colPaid.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getDiscountedPrice()).asObject());
        colPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colDiscount.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(ProgramHelpers.get2DecimalDouble(
                        cellData.getValue().getPrice() - cellData.getValue().getDiscountedPrice())).asObject());
        colExpiration.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExpirationDate()));
        colBrand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBrand()));
        colMovementType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMovementType()));

        productTable.setItems(products);
        ProgramHelpers.adjustTableHeight(productTable);

    }

    public void closeWindow() {
        Stage stage = (Stage) lblInvoiceNumber.getScene().getWindow();
        stage.close();
    }
}
