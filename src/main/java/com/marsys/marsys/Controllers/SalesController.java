package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.TableViewHelper;
import com.marsys.marsys.Models.*;
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
import java.util.*;
import java.util.function.UnaryOperator;

public class SalesController implements Initializable {
    private double couponDiscount = 0.0;
    private double discountAmount = 0.0;
    private double lastTotal = 0.0;
    private double actualCartTotal = 0.0;
    Employee user = Session.getInstance().getCurrentUser();
    double total = 0.00;
    @FXML
    private Button btnApplyCoupon;
    @FXML
    private Label lblLastTotal;
    @FXML
    private Label lblDiscountTotal;
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
    private TableColumn<Product, String> colCategory;
    @FXML
    private TableColumn<Product, String> colBrand;
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

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    Repository _repository = new Repository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        barcodeField.setOnAction(event -> onAddProduct());
        quantityField.setOnAction(event -> onAddProduct());
        couponField.setOnAction(event -> applyCoupon());

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,2}")) {
                return change;
            }
            return null;
        };
        UnaryOperator<TextFormatter.Change> filter2 = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,3}")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter2 = new TextFormatter<>(filter2);
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        quantityField.setTextFormatter(textFormatter);
        barcodeField.setTextFormatter(textFormatter2);

        colBarcode.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.2));
        colProductName.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.3));
        colQuantity.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colCategory.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colCategory.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colPrice.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.1));
        colAction.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.08));

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());

        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));

        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));

        colBrand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBrand()));

        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());


        salesTable.setItems(productList);
        TableViewHelper.adjustTableHeight(salesTable);
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
                int inventoryQuantity = Integer.parseInt(_repository.getInventoryCellByBarcode("QUANTITY", scannedProduct.getBarcode()));

                boolean found = false;

                for (Product p : productList) {

                    if (p.getBarcode().equals(scannedProduct.getBarcode())) {
                        if (p.getQuantity() + scannedProduct.getQuantity() <= inventoryQuantity) {
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
                    if (scannedProduct.getQuantity() <= inventoryQuantity) {
                        Product newProduct = _repository.getProductModelByBarcode(scannedBarcode);
                        newProduct.setQuantity(Integer.parseInt(quantityField.getText()));
                        productList.add(newProduct);
                        total += newProduct.getPrice();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Alert");
                        alert.setHeaderText("Failed");
                        alert.setContentText("Not enough stock for this product!");
                        alert.showAndWait();
                        return;
                    }

                }


                lblTotal.setText(total + " TL");

                barcodeField.clear();
                quantityField.setText("1");
                barcodeField.requestFocus();
                TableViewHelper.adjustTableHeight(salesTable);
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
                            TableViewHelper.adjustTableHeight(salesTable);
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
                paymentModalController.setPaymentTotal(lastTotal);

                paymentModalController.setPaymentCompleteListener(cardNumber -> completeSale(cardNumber, lastTotal));
                Stage stage = new Stage();
                stage.setTitle("Payment");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Program Error");
                alert.setHeaderText("An error occured in this operation.");
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't added a product to sale!");
            alert.showAndWait();
        }
        TableViewHelper.adjustTableHeight(salesTable);

    }

    public void completeSale(String cardNumber, Double newTotal) {
        try {
            if (salesTable.getItems() != null && !salesTable.getItems().isEmpty()) {
                String movementType = "SALE";
                String invoiceNumber = _repository.getLatestInvoiceNumber();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                String date = sdf.format(new Date());

                for (Product p : productList) {
                    for (int i = 1; i <= p.getQuantity(); i++) {
                        _repository.insertIntoStockMovementTable(_repository.getLatestMovementId(), movementType, p, invoiceNumber, user.getId(), date);
                    }
                    _repository.reduceStockQuantity(p);
                }
                if (couponField.getText() != null) {
                    _repository.updateCouponUsed(couponField.getText());
                }
                _repository.insertIntoInvoicesTable(
                        invoiceNumber,
                        user,
                        cardNumber,
                        cardNumber,
                        String.format(Locale.US, "%.2f", newTotal),
                        date,
                        String.format(Locale.US, "%.2f", discountAmount),
                        String.format(Locale.US, "%.2f", actualCartTotal)
                );

                total = 0.00;
                lblTotal.setText(total + " TL");

                barcodeField.clear();
                productList.clear();
                actualCartTotal = 0;
                lastTotal = 0;
                couponDiscount = 0;
                discountAmount = 0;
                lblLastTotal.setText(String.format("%.2f TL", lastTotal));
                lblDiscountTotal.setText(String.format("%.2f TL", discountAmount));
                lblTotal.setText(String.format("%.2f TL", actualCartTotal));
                salesTable.getItems().clear();
                salesTable.refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setHeaderText("Sale is completed");
                alert.setContentText("Invoice Number: " + invoiceNumber);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No record");
                alert.setContentText("You haven't added a product to sale!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        TableViewHelper.adjustTableHeight(salesTable);

    }

    @FXML
    private void cashPayment() {
        if (salesTable.getItems() != null && !salesTable.getItems().isEmpty()) {
            Double lastDiscountedTotal = lastTotal * 0.75;
            if (_repository.getCampaignModelById("005").getIsActive().equals("ACTIVE")) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Discount Confirmation");
                confirmation.setHeaderText("Apply 15% Discount?");
                confirmation.setContentText("A 15% discount will be applied.\n" +
                        "Original Total: " + String.format("%.2f", lastTotal) + "\n" +
                        "Discounted Total: " + String.format("%.2f", lastDiscountedTotal));

                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                ButtonType backToCartButton = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);

                confirmation.getButtonTypes().setAll(yesButton, noButton, backToCartButton);

                Optional<ButtonType> result = confirmation.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == yesButton) {
                        discountAmount += lastTotal - lastDiscountedTotal;
                        completeSale("000000", lastDiscountedTotal);
                    } else if (result.get() == noButton) {
                        completeSale("000000", lastTotal);
                    }
                }
            } else {
                completeSale("000000", lastTotal);
            }


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
                    actualCartTotal = 0;
                    lastTotal = 0;
                    couponDiscount = 0;
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
    public void back() {
        LayoutController _layoutController = new LayoutController();
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }

    private void campaignChecker() {
        double newTotal = 0.0;
        actualCartTotal = 0.0;

        for (Product p : productList) {
            Product original = _repository.getProductModelByBarcode(p.getBarcode());
            p.setPrice(original.getPrice());
            p.setDiscounted(false);
            p.setDiscountedPrice(0.0);
            actualCartTotal += p.getQuantity() * p.getPrice();
        }

        for (Product p : productList) {
            int quantity = p.getQuantity();
            double price = p.getPrice();

            Campaign halfOff = _repository.get50CampaignForProduct(p.getBarcode());
            if (halfOff != null && quantity >= 1) {
                double total = 0.0;
                for (int i = 1; i <= quantity; i++) {
                    if (i == 2) {
                        total += price * 0.5;
                    } else {
                        total += price;
                    }
                }
                p.setDiscountedPrice(total);
                p.setDiscounted(true);
                continue;
            }

            Campaign buy2Get1 = _repository.getBuy2Get1CampaignByBarcode(p.getBarcode());
            if (buy2Get1 != null && quantity >= 2) {
                int freeItems = quantity / 2;
                int paidItems = quantity - freeItems;
                double total = paidItems * price;

                p.setDiscountedPrice(total);
                p.setDiscounted(true);
                continue;
            }

            p.setDiscountedPrice(0.0);
        }

        Map<String, List<Product>> categoryMap = new HashMap<>();
        for (Product p : productList) {
            if (!p.isDiscounted()) {
                categoryMap.computeIfAbsent(p.getCategory(), k -> new ArrayList<>()).add(p);
            }
        }

        for (Map.Entry<String, List<Product>> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            List<Product> products = entry.getValue();

            Campaign halfOffCategory = _repository.get50CampaignForCategory(category);
            if (halfOffCategory != null) {
                int counter = 0;
                int discountIndex = 1;

                for (Product p : products) {
                    int q = p.getQuantity();
                    double price = p.getPrice();
                    double discountedPrice = 0.0;

                    for (int i = 0; i < q; i++) {
                        if (counter == discountIndex) {
                            discountedPrice += price * 0.5;
                        } else {
                            discountedPrice += price;
                        }
                        counter++;
                    }

                    p.setDiscountedPrice(discountedPrice);
                    p.setDiscounted(true);
                }
            } else {
                for (Product p : products) {
                    p.setDiscountedPrice(p.getQuantity() * p.getPrice());
                }
            }
        }

        for (Product p : productList) {
            newTotal += p.getDiscountedPrice();
        }

        newTotal -= couponDiscount;
        if (newTotal < 0) newTotal = 0;

        lastTotal = newTotal;
        lblLastTotal.setText(String.format("%.2f TL", lastTotal));
        discountAmount = actualCartTotal - lastTotal;
        lblDiscountTotal.setText(String.format("%.2f TL", discountAmount));
        total = actualCartTotal;
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
            couponDiscount = 0.0;
            campaignChecker();
            btnApplyCoupon.setText("Apply");
            couponField.setEditable(true);
            couponField.clear();
        }
    }

}
