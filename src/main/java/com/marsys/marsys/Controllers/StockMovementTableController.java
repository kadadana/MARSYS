package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Models.StockMovement;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;
import com.marsys.marsys.Helpers.TableViewHelper;

import java.util.stream.Collectors;

public class StockMovementTableController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();
    LayoutController layoutController = new LayoutController();
    RepositoryMete repositoryMete = new RepositoryMete();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private Button btnClear;
    @FXML
    private TableView<StockMovement> stockMovementTable;
    @FXML
    private TableColumn<StockMovement, String> colBarcode;
    @FXML
    private TableColumn<StockMovement, String> colProductName;
    @FXML
    private TableColumn<StockMovement, String> colQuantity;
    @FXML
    private TableColumn<StockMovement, String> colSalePrice;
    @FXML
    private TableColumn<StockMovement, String> colCategory;
    @FXML
    private TableColumn<StockMovement, String> colBrand;
    @FXML
    private TableColumn<StockMovement, String> colBuyingPrice;
    @FXML
    private TableColumn<StockMovement, String> colExpiration;
    @FXML
    private TableColumn<StockMovement, String> colMovementType;
    @FXML
    private TableColumn<StockMovement, String> colInvoiceNumber;
    @FXML
    private TableColumn<StockMovement, String> colUser;
    @FXML
    private TableColumn<StockMovement, String> colDate;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnSearch;
    @FXML
    private TextField barcodeField;
    @FXML
    private DatePicker firstDatePicker;
    @FXML
    private DatePicker lastDatePicker;


    private ObservableList<StockMovement> stockMovementList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClear.setVisible(false);
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        stockMovementList.addAll(repositoryMete.getStockMovementList());
        firstDatePicker.getEditor().setDisable(true);
        lastDatePicker.getEditor().setDisable(true);
        colBarcode.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.06));
        colProductName.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.12));
        colQuantity.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.05));
        colSalePrice.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.08));
        colCategory.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.08));
        colBrand.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.06));
        colBuyingPrice.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.08));
        colExpiration.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.08));
        colMovementType.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.11));
        colInvoiceNumber.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.10));
        colUser.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.04));
        colDate.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.12));

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));
        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        colQuantity.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getQuantity()));
        colSalePrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrice()));
        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));
        colBrand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBrand()));
        colBuyingPrice.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBuyingPrice()));
        colExpiration.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExpirationDate()));
        colMovementType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMovementType()));
        colInvoiceNumber.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getInvoiceNumber()));
        colUser.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser()));
        colDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate()));

        stockMovementTable.setItems(stockMovementList);
        stockMovementTable.getItems().addListener(new ListChangeListener<StockMovement>() {
            @Override
            public void onChanged(Change<? extends StockMovement> c) {
                TableViewHelper.adjustTableHeight(stockMovementTable);
            }
        });

        TableViewHelper.adjustTableHeight(stockMovementTable);
    }

    @FXML
    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/stockandinventory.fxml", btnBack);
    }

    @FXML
    private void search() {
        String barcodeText = barcodeField.getText();
        if (barcodeText == null) {
            barcodeText = "";
        }
        LocalDate firstDate = firstDatePicker.getValue();
        LocalDate lastDate = lastDatePicker.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String strFirstDate = null;
        String strLastDate = null;

        if (firstDate != null && lastDate != null) {
            strFirstDate = firstDate.format(formatter);
            strLastDate = lastDate.format(formatter);
        }
        stockMovementList.clear();
        if (firstDatePicker.getValue() != null && lastDatePicker.getValue() != null && (barcodeField.getText() == null || barcodeField.getText().isBlank())) {
            stockMovementList.addAll(repositoryMete.getStockMovementListBySearch("DATE", strFirstDate, strLastDate, null));
        } else if (!barcodeField.getText().isBlank()) {
            stockMovementList.addAll(repositoryMete.getStockMovementListBySearch("BARCODE", null, null, barcodeField.getText()));
        } else if (firstDatePicker.getValue() != null && lastDatePicker.getValue() != null && !barcodeField.getText().isBlank()) {
            stockMovementList.addAll(repositoryMete.getStockMovementListBySearch("BOTH", strFirstDate, strLastDate, barcodeField.getText()));
        } else {
            stockMovementList.addAll(repositoryMete.getStockMovementList());
        }
        btnClear.setVisible(true);
        stockMovementTable.refresh();

        TableViewHelper.adjustTableHeight(stockMovementTable);

    }

    @FXML
    private void clearSearch() {
        firstDatePicker.setValue(null);
        lastDatePicker.setValue(null);
        barcodeField.setText(null);
        btnClear.setVisible(false);
        stockMovementList.clear();
        stockMovementList.addAll(repositoryMete.getStockMovementList());
        stockMovementTable.refresh();

        TableViewHelper.adjustTableHeight(stockMovementTable);

    }

}
