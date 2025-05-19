package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.TableViewHelper;
import com.marsys.marsys.Models.Invoice;
import com.marsys.marsys.Models.Product;
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

import java.util.List;

public class InvoiceDetailsController {
    @FXML
    private Label lblInvoiceNumber;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblDiscountTotal;
    @FXML
    private Label lblLastTotal;
    @FXML
    private TableView<Product> productTable;
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
    private TableColumn<Product, Double> colBuyingPrice;
    @FXML
    private TableColumn<Product, String> colExpiration;
    @FXML
    private TableColumn<Product, Double> colPrice;

    private final ObservableList<Product> products = FXCollections.observableArrayList();

    public void setInvoice(Invoice invoice, List<Product> productList) {
        lblInvoiceNumber.setText(invoice.getInvoiceNumber());
        lblTotal.setText(invoice.getActualCartAmount());
        lblDiscountTotal.setText(invoice.getDiscountAmount());
        lblLastTotal.setText(invoice.getPaidAmount());
        products.addAll(productList);

        colBarcode.prefWidthProperty().bind(productTable.widthProperty().multiply(0.10));
        colProductName.prefWidthProperty().bind(productTable.widthProperty().multiply(0.20));
        colQuantity.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));
        colCategory.prefWidthProperty().bind(productTable.widthProperty().multiply(0.13));
        colPrice.prefWidthProperty().bind(productTable.widthProperty().multiply(0.07));
        colBrand.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));
        colBuyingPrice.prefWidthProperty().bind(productTable.widthProperty().multiply(0.12));
        colExpiration.prefWidthProperty().bind(productTable.widthProperty().multiply(0.19));

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));

        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));

        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colBuyingPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getBuyingPrice()).asObject());
        colExpiration.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExpirationDate()));
        colBrand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBrand()));

        productTable.setItems(products);
        TableViewHelper.adjustTableHeight(productTable);

    }

    public void closeWindow() {
        Stage stage = (Stage) lblInvoiceNumber.getScene().getWindow();
        stage.close();
    }
}
