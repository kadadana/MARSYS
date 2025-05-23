package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.ProgramHelpers;
import com.marsys.marsys.Models.*;
import com.marsys.marsys.Repository.Repository;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ReturnsController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();
    Repository repository = new Repository();
    RepositoryMete repositoryMete = new RepositoryMete();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblDate;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnFetch;
    @FXML
    private Button btnFind;
    @FXML
    private TextField invoiceField;
    @FXML
    private TextField barcodeField;
    @FXML
    TableView<Product> productTable;
    @FXML
    TableColumn<Product, String> colBarcode;
    @FXML
    TableColumn<Product, String> colProductName;
    @FXML
    TableColumn<Product, String> colBrand;
    @FXML
    TableColumn<Product, String> colCategory;
    @FXML
    TableColumn<Product, Double> colPrice;
    @FXML
    TableColumn<Product, String> colAction;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblDiscountTotal;
    @FXML
    private Label lblLastTotal;
    @FXML
    private Label lblTotalRefunded;
    @FXML
    private Label lblRefunding;

    private double refunding;
    private Invoice invoice;
    double returningActualCartAmount = 0.00;
    double returningDiscountTotal = 0.00;
    double returningPaidAmount = 0.00;
    double refundedTotal = 0.00;


    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        invoiceField.setOnAction(event -> fetchInvoice());
        barcodeField.setOnAction(event -> addToReturn());
        barcodeField.setDisable(true);
        btnFind.setDisable(true);

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

        colPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colAction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMovementType()));

        colBarcode.prefWidthProperty().bind(productTable.widthProperty().multiply(0.2));
        colProductName.prefWidthProperty().bind(productTable.widthProperty().multiply(0.3));
        colCategory.prefWidthProperty().bind(productTable.widthProperty().multiply(0.1));
        colCategory.prefWidthProperty().bind(productTable.widthProperty().multiply(0.1));
        colPrice.prefWidthProperty().bind(productTable.widthProperty().multiply(0.1));
        colAction.prefWidthProperty().bind(productTable.widthProperty().multiply(0.08));


        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (product == null || empty) {
                    setStyle("");
                } else if (product.isRecentlyAdded() && "RETURN".equalsIgnoreCase(product.getMovementType())) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });

    }

    @FXML
    private void fetchInvoice() {
        if (invoiceField.getText() != null || !invoiceField.getText().isBlank()) {
            invoice = repository.getInvoiceModelByInvoiceNumber(invoiceField.getText());
            List<StockMovement> stockMovementList = repositoryMete.getStockMovementListBySearching("03",
                    ProgramHelpers.getStringDateByLocalDate(LocalDate.now().minusDays(15)),
                    ProgramHelpers.getStringDateByLocalDate(LocalDate.now()),
                    null, invoiceField.getText());
            if (stockMovementList != null && !stockMovementList.isEmpty()) {
                for (StockMovement sm : stockMovementList) {
                    Product product = repository.getProductModelByBarcode(sm.getBarcode());
                    double ratedPrice = product.getPrice() * Double.parseDouble(invoice.getPaidAmount())
                            / Double.parseDouble(invoice.getActualCartAmount());


                    product.setMovementType(sm.getMovementType());
                    product.setPrice(ProgramHelpers.get2DecimalDouble(ratedPrice));


                    productList.add(product);

                }
                for (Product p : productList) {
                    if (p.getMovementType().equalsIgnoreCase("RETURN")) {
                        refundedTotal += p.getPrice();

                    }
                }

                lblDate.setText("Date: " + invoice.getDate());
                productTable.setItems(productList);
                lblTotal.setText(invoice.getActualCartAmount());
                lblDiscountTotal.setText(invoice.getDiscountAmount());
                lblLastTotal.setText(invoice.getPaidAmount());
                lblTotalRefunded.setText(Double.toString(refundedTotal));
                invoiceField.setDisable(true);
                btnFetch.setDisable(true);
                barcodeField.setDisable(false);
                btnFind.setDisable(false);

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText("Failed");
                alert.setContentText("This invoice has expired!");
                alert.showAndWait();
            }


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert");
            alert.setHeaderText("Failed");
            alert.setContentText("Enter an invoice number!");
            alert.showAndWait();
        }
        ProgramHelpers.adjustTableHeight(productTable);
    }

    @FXML
    private void addToReturn() {

        int saleCount = 0;
        int returnCount = 0;
        boolean existsInInvoice = false;

        if (!barcodeField.getText().isBlank() && barcodeField.getText() != null) {
            Product returningProduct = repository.getProductModelByBarcode(barcodeField.getText());

            if (returningProduct != null) {
                for (Product p : productTable.getItems()) {
                    if (p.getBarcode().equals(returningProduct.getBarcode())) {
                        existsInInvoice = true;
                        returningProduct.setPrice(p.getPrice());
                        String movementType = p.getMovementType();
                        if ("SALE".equalsIgnoreCase(movementType)) {
                            saleCount++;
                        } else if ("RETURN".equalsIgnoreCase(movementType)) {
                            returnCount++;
                        }
                    }
                }

                if (!existsInInvoice) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Alert");
                    alert.setHeaderText("Failed");
                    alert.setContentText("This product is not in this invoice!");
                    alert.showAndWait();
                    return;
                }
                if (returnCount >= saleCount) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Alert");
                    alert.setHeaderText("Failed");
                    alert.setContentText("This product has already been returned!");
                    alert.showAndWait();
                    return;
                }
                returningProduct.setMovementType("RETURN");
                returningProduct.setRecentlyAdded(true);
                productList.add(returningProduct);
                returningPaidAmount += ProgramHelpers.get2DecimalDouble(returningProduct.getPrice());
                lblRefunding.setText(Double.toString(returningPaidAmount));
                returningActualCartAmount += ProgramHelpers.get2DecimalDoubleFromString(invoice.getActualCartAmount());
                returningDiscountTotal += ProgramHelpers.get2DecimalDouble(returningActualCartAmount - returningPaidAmount);
                ProgramHelpers.adjustTableHeight(productTable);

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText("Failed");
                alert.setContentText("This product is not in this invoice!");
                alert.showAndWait();
            }


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alert");
            alert.setHeaderText("Failed");
            alert.setContentText("Enter a barcode!");
            alert.showAndWait();
        }
    }

    @FXML
    private void back() {
        LayoutController _layoutController = new LayoutController();
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }

    @FXML
    private void cancelProcess() {
        if (productTable.getItems() != null && !productTable.getItems().isEmpty()) {
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
                    lblLastTotal.setText("0.00");
                    lblTotal.setText("0.00");
                    lblTotalRefunded.setText("0.00");
                    lblDiscountTotal.setText("0.00");
                    lblRefunding.setText("0.00");
                    lblDate.setText("");
                    productTable.refresh();
                    invoiceField.setDisable(false);
                    invoiceField.setText(null);
                    btnFetch.setDisable(false);
                    returningPaidAmount = 0.00;
                    returningActualCartAmount = 0.00;
                    returningDiscountTotal = 0.00;
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't started a process");
            alert.showAndWait();
        }
    }

    @FXML
    private void completeReturn() {

        List<Product> recentlyAddedProducts = productTable.getItems().stream()
                .filter(p -> p.isRecentlyAdded() && "Return".equalsIgnoreCase(p.getMovementType()))
                .toList();

        if (recentlyAddedProducts.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't added a product to return");
            alert.showAndWait();
            return;
        }
        for (Product product : recentlyAddedProducts) {
            StockMovement stockMovement = new StockMovement(repository.getLatestMovementId(),
                    "RETURN",
                    product.getBarcode(),
                    invoiceField.getText(),
                    user.getId(),
                    ProgramHelpers.getStringDateTimeByLocalDateTime(LocalDateTime.now()));

            repository.insertIntoStockMovementTable(stockMovement);
            repositoryMete.updateStockQuantity(product, false);
        }

        String exInvoiceNumber = invoice.getInvoiceNumber();
        invoice.setOriginalInvoiceNumber(invoice.getInvoiceNumber());
        invoice.setInvoiceNumber(repository.getLatestInvoiceNumber());
        invoice.setActualCartAmount(ProgramHelpers.get2DecimalString(returningActualCartAmount));
        invoice.setDiscountAmount(ProgramHelpers.get2DecimalString(returningDiscountTotal));
        invoice.setPaidAmount(ProgramHelpers.get2DecimalString(returningPaidAmount));
        invoice.setDate(ProgramHelpers.getStringDateTimeByLocalDateTime(LocalDateTime.now()));

        repository.insertIntoInvoicesTable(invoice);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Completed");
        alert.setHeaderText("Return is completed");
        alert.setContentText("Return Invoice Number: " + invoice.getInvoiceNumber() + "\n" +
                "Original Invoice Number: " + exInvoiceNumber);
        alert.showAndWait();

        productList.clear();
        lblLastTotal.setText("0.00");
        lblTotal.setText("0.00");
        lblTotalRefunded.setText("0.00");
        lblDiscountTotal.setText("0.00");
        lblRefunding.setText("0.00");
        lblDate.setText("");
        productTable.refresh();
        invoiceField.setDisable(false);
        invoiceField.setText(null);
        btnFetch.setDisable(false);
        returningPaidAmount = 0.00;
        returningActualCartAmount = 0.00;
        returningDiscountTotal = 0.00;
    }


}
