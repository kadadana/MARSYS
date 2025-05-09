package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.*;
import com.marsys.marsys.Repository.Repository;
import eu.hansolo.tilesfx.skins.TestTileSkin;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class SalesController implements Initializable {
    private Coupon appliedCoupon = null;
    private double couponDiscount = 0.0;
    Employee user = Session.getInstance().getCurrentUser();
    double total = 0.00;
    @FXML
    private Button btnApplyCoupon;
    @FXML
    private TextField couponField;
    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private TableView<Product> salesTable;
    @FXML
    private TableColumn<Product, String> colBarcode;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, String> colTaxRate;
    @FXML
    private TableColumn<Product, Integer> colQuantity;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Void> colAction;
    @FXML
    private TextField barcodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnBack;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    Repository _repository = new Repository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        barcodeField.setOnAction(event -> onAddProduct());
        quantityField.setOnAction(event -> onAddProduct());

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,2}")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        quantityField.setTextFormatter(textFormatter);

        colBarcode.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.2));
        colProductName.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.4));
        colQuantity.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colTaxRate.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colPrice.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colAction.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.08));

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));

        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        ;

        colTaxRate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTaxRate()));

        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());


        salesTable.setItems(productList);
        addDeleteButtonToTable();

    }

    @FXML
    public void onAddProduct() {
        Product scannedProduct;
        String scannedBarcode = barcodeField.getText();
        if (!barcodeField.getText().isBlank() && !quantityField.getText().isBlank()) {
            if (_repository.getInventoryCellByBarcode("BARCODE", scannedBarcode) == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText("Failed");
                alert.setContentText("There is no product assigned to this barcode!");
                alert.showAndWait();
            } else {
                scannedProduct = _repository.getProductModelByBarcode(scannedBarcode);
                scannedProduct.setQuantity(Integer.parseInt(quantityField.getText()));
                boolean found = false;

                for (Product p : productList) {

                    if (p.getBarcode().equals(scannedProduct.getBarcode())) {
                        if (p.getQuantity() < Integer.parseInt(_repository.getInventoryCellByBarcode("QUANTITY", scannedProduct.getBarcode()))) {
                            p.setQuantity(p.getQuantity() + Integer.parseInt(quantityField.getText()));
                            total += scannedProduct.getPrice() * Integer.parseInt(quantityField.getText());
                            found = true;
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Alert");
                            alert.setHeaderText("Failed");
                            alert.setContentText("Not enough stock for this product!");
                            alert.showAndWait();
                            return;
                        }
                        barcodeField.clear();
                        break;
                    }
                }
                if (!found) {
                    Product newProduct = _repository.getProductModelByBarcode(scannedBarcode);
                    newProduct.setQuantity(Integer.parseInt(quantityField.getText()));
                    productList.add(newProduct);
                    total += newProduct.getPrice();
                }


                lblTotal.setText(total + " TL");

                barcodeField.clear();
                quantityField.setText("1");
                barcodeField.requestFocus();
                campaignChecker();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert");
            alert.setHeaderText("Failed");
            alert.setContentText("Fill the fields, please!");
            alert.showAndWait();
        }

    }

    private void addDeleteButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete?");
                    alert.setHeaderText("Are you sure to delete this from the cart?");
                    alert.setContentText(product.getProductName() + " (" + product.getQuantity() + ")");

                    ButtonType yes = new ButtonType("Yes");
                    ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(yes, no);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == yes) {
                            productList.remove(product);
                            campaignChecker();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    @FXML
    private void cardPayment() {
        if (salesTable.getItems() != null && !salesTable.getItems().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/paymentmodal.fxml"));

                Parent root = loader.load();

                PaymentModalController paymentModalController = loader.getController();
                paymentModalController.setPaymentTotal(total);

                paymentModalController.setPaymentCompleteListener(new PaymentModalController.PaymentCompleteListener() {
                    @Override
                    public void onPaymentComplete(String cardNumber) {
                        completeSale(cardNumber);
                    }
                });
                Stage stage = new Stage();
                stage.setTitle("Payment");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't added a product to sale!");
            alert.showAndWait();
        }

    }

    public void completeSale(String cardNumber) {
        try {
            if (salesTable.getItems() != null && !salesTable.getItems().isEmpty()) {
                String movementType = "SALE";
                String invoiceNumber = _repository.getLatestInvoiceNumber();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                String date = sdf.format(new Date());

                for (Product p : productList) {
                    _repository.insertIntoStockMovementTable(p, movementType, invoiceNumber, user.getId(), date);
                    _repository.reduceStockQuantity(p);
                }

                _repository.insertIntoInvoicesTable(invoiceNumber, user, cardNumber, cardNumber, Double.toString(total), date);
                total = 0.00;
                lblTotal.setText(total + " TL");

                barcodeField.clear();
                productList.removeAll();
                salesTable.getItems().clear();
                salesTable.refresh();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No record");
                alert.setContentText("You haven't added a product to sale!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void cashPayment() {
        if (salesTable.getItems() != null && !salesTable.getItems().isEmpty()) {
            completeSale("000000");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't added a product to sale!");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancelProcess() {
        if (salesTable.getItems() != null && !salesTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel");
            alert.setHeaderText("Are you sure to cancel this process?");
            alert.setContentText("This will clear the table.");

            ButtonType buttonYes = new ButtonType("Yes");
            ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonYes, buttonNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonYes) {
                    productList.clear();
                    total = 0;
                    campaignChecker();
                    salesTable.refresh();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't added a product to sale!");
            alert.showAndWait();
        }

    }

    @FXML
    private void back() {
        LayoutController _layoutController = new LayoutController();
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }

    private void campaignChecker() {
        double newTotal = 0.0;

        for (Product p : productList) {
            Product original = _repository.getProductModelByBarcode(p.getBarcode());
            p.setPrice(original.getPrice());
            p.setDiscounted(false);
            p.setDiscountedPrice(0.0);
        }

        for (Product p : productList) {
            int quantity = p.getQuantity();
            double price = p.getPrice();

            Campaign buy2Get1 = _repository.getBuy2Get1CampaignByBarcode(p.getBarcode());
            if (buy2Get1 != null) {
                int paidItems = quantity - (quantity / 2);
                double total = paidItems * price;
                p.setDiscountedPrice(total);
                p.setDiscounted(true);
                continue;
            }

            Campaign halfOff = _repository.get50CampaignForProduct(p.getBarcode());
            if (halfOff != null && quantity >= 2) {
                int fullPriceItems = quantity - 1;
                double total = (fullPriceItems * price) + (price * 0.5);
                p.setDiscountedPrice(total);
                p.setDiscounted(true);
                continue;
            }

            p.setDiscountedPrice(quantity * price);
        }

        for (Product p : productList) {
            newTotal += p.getDiscountedPrice();
        }

        newTotal -= couponDiscount;
        if (newTotal < 0) newTotal = 0;

        total = newTotal;
        lblTotal.setText(String.format("%.2f TL", total));
        salesTable.refresh();
    }

    @FXML
    private void applyCoupon() {
        Button applyButton = btnApplyCoupon;
        String couponCode = couponField.getText().trim();

        if (applyButton.getText().equals("Apply")) {
            if (couponCode.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a coupon code!!");
                alert.showAndWait();
            } else {
                Coupon coupon = _repository.getValidCoupon(couponCode);
                if (coupon != null) {
                    appliedCoupon = coupon;
                    couponDiscount = Double.parseDouble(coupon.getDiscountAmount());
                    campaignChecker();
                    btnApplyCoupon.setText("Remove Coupon");
                    couponField.setEditable(false);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Coupon");
                    alert.setHeaderText(null);
                    alert.setContentText("This coupon code is not valid!!");
                    alert.showAndWait();
                }
            }


        } else if (applyButton.getText().equals("Remove Coupon")) {
            appliedCoupon = null;
            couponDiscount = 0.0;
            campaignChecker();
            btnApplyCoupon.setText("Apply");
            couponField.setEditable(true);
            couponField.clear();
        }
    }

}
