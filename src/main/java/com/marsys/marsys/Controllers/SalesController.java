package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
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

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class SalesController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();
    double total = 0.00;

    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
    @FXML
    private TableView<Product> salesTable;
    @FXML
    private TableColumn<Product, String> colBarcode;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, String> colDiscount;
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
    private Label lblTotal;
    @FXML
    private Button btnBack;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    Repository _repository = new Repository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colBarcode.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.2));
        colProductName.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.3));
        colQuantity.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colDiscount.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colTaxRate.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colPrice.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colAction.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.090));

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));

        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        colDiscount.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiscountRate()));

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
        String barcode = barcodeField.getText();
        if (_repository.getCellInventoryByBarcode("BARCODE", barcode) == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert");
            alert.setHeaderText("Failed");
            alert.setContentText("There is no product assigned to this barcode!");
            alert.showAndWait();
            return;
        }

        String productName = _repository.getCellInventoryByBarcode("NAME", barcode);
        double price = Double.parseDouble(_repository.getCellInventoryByBarcode("SALE_PRICE", barcode));
        String category = _repository.getCellInventoryByBarcode("CATEGORY", barcode);
        String brand = _repository.getCellInventoryByBarcode("BRAND", barcode);
        double buyingPrice = Double.parseDouble(_repository.getCellInventoryByBarcode("BUYING_PRICE", barcode));
        String expirationDate = _repository.getCellInventoryByBarcode("EXPIRATION", barcode);

        boolean found = false;

        for (Product p : productList) {

            if (p.getBarcode().equals(barcode)) {
                if (p.getQuantity() < Integer.parseInt(_repository.getCellInventoryByBarcode("QUANTITY", barcode))) {
                    p.setQuantity(p.getQuantity() + 1);
                    total += price;
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
            Product newProduct = new Product(barcode, productName, 1, price, category, brand, buyingPrice, expirationDate, "18%", "%0");
            productList.add(newProduct);
            total += price;
        }


        lblTotal.setText(total + " TL");

        barcodeField.clear();

        salesTable.refresh();

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

                            total -= product.getQuantity() * product.getPrice();
                            lblTotal.setText(total + " TL");
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
                String invoiceNumber = _repository.getRecentInvoiceNumber();
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
}
