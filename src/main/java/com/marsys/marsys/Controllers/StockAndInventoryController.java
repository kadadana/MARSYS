package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.TableViewHelper;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StockAndInventoryController implements Initializable {
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
    private TableColumn<Product, Integer> colQuantity;
    @FXML
    private TableColumn<Product, String> colBrand;
    @FXML
    private TableColumn<Product, Void> colAction;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnProductEntry;
    @FXML
    private Button btnGoToStockMovementTable;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        productList.addAll(repositoryMete.getAllStockList());

        colBarcode.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.1));
        colProductName.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.3));
        colQuantity.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.2));
        colAction.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.1));
        colBrand.prefWidthProperty().bind(stockTable.widthProperty().multiply(0.28));


        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));
        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colBrand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBrand()));

        stockTable.setItems(productList);
        TableViewHelper.adjustTableHeight(stockTable);
        addProductInfoButtonsToTable();

    }

    @FXML
    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }

    @FXML
    public void goToProductEntry() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/productentry.fxml", btnProductEntry);
    }

    @FXML
    public void goToStockMovementTable() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/stockmovementtable.fxml", btnGoToStockMovementTable);

    }

    private void openProductInfoModal(String barcode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/productdetails.fxml"));
            Parent root = loader.load();


            ProductDetailsController controller = loader.getController();
            controller.setProduct(repositoryMete.getProductModelByBarcode(barcode));

            Stage stage = new Stage();
            stage.setWidth(400);
            stage.setHeight(400);
            stage.setResizable(false);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Product Detail");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    private void addProductInfoButtonsToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Product Details");

            {
                btn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    openProductInfoModal(product.getBarcode());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }


}
