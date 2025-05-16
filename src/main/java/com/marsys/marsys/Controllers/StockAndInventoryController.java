package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.TableViewHelper;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.net.URL;
import java.util.ResourceBundle;

public class StockAndInventoryController implements Initializable{
    Employee user = Session.getInstance().getCurrentUser();
    LayoutController layoutController = new LayoutController();
    RepositoryMete repositoryMete = new RepositoryMete();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;

    @FXML
    private TableView<Product> stockTable;
    @FXML
    private TableColumn<Product, String> colBarcode;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, String> colQuantity;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnProductEntry;
    @FXML
    private Button btnGoToStockMovementTable;

    private ObservableList<Product> productList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        productList.addAll(repositoryMete.getAllStockList());

        colBarcode.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.3));
        colProductName.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.4));
        colQuantity.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.28));

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));
        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        colQuantity.setCellValueFactory(cellData ->
                new SimpleStringProperty(Integer.toString(cellData.getValue().getQuantity())));
        stockTable.setItems(productList);
        TableViewHelper.adjustTableHeight(stockTable);

    }

    @FXML
    public void back(){
        layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }
    @FXML
    public void goToProductEntry(){
        layoutController.loadPageByButton("/com/marsys/marsys/Views/productentry.fxml", btnProductEntry);
    }
    @FXML
    public void goToStockMovementTable(){
        layoutController.loadPageByButton("/com/marsys/marsys/Views/stockmovementtable.fxml", btnGoToStockMovementTable);

    }

}
