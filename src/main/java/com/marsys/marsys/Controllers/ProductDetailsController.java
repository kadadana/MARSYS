package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductDetailsController implements Initializable {

    @FXML
    private Label lblBarcode;
    @FXML
    private Label lblProductName;
    @FXML
    private Label lblQuantity;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblBrand;
    @FXML
    private Label lblBuyingPrice;
    @FXML
    private Label lblExpirationDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setProduct(Product product) {
        lblBarcode.setText(product.getBarcode());
        lblProductName.setText(product.getProductName());
        lblQuantity.setText(String.valueOf(product.getQuantity()));
        lblPrice.setText(String.format("%.2f", product.getPrice()));
        lblCategory.setText(product.getCategory());
        lblBrand.setText(product.getBrand());
        lblBuyingPrice.setText(String.format("%.2f", product.getBuyingPrice()));
        lblExpirationDate.setText(product.getExpirationDate() != null ? product.getExpirationDate() : "-");
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) lblBarcode.getScene().getWindow();
        stage.close();
    }
}
