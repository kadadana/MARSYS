package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.ProgramHelpers;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ProductEntryController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();
    LayoutController layoutController = new LayoutController();
    RepositoryMete _repository = new RepositoryMete();

    private Integer total = 0;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
    @FXML
    private Button btnBack;
    @FXML
    private TableView<Product> productEntryTable;
    @FXML
    private TableColumn<Product, String> colBarcode;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, Integer> colQuantity;
    @FXML
    private TableColumn<Product, Void> colAction;
    @FXML
    private TextField barcodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private Label lblTotal;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        barcodeField.setOnAction(event -> onAddProduct());
        quantityField.setOnAction(event -> onAddProduct());

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        lblTotal.setText(total.toString());


        UnaryOperator<TextFormatter.Change> inputFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]?)?")) {
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

        quantityField.setTextFormatter(new TextFormatter<>(inputFilter));
        barcodeField.setTextFormatter(new TextFormatter<>(filter2));

        colBarcode.prefWidthProperty().bind(productEntryTable.widthProperty().multiply(0.30));
        colProductName.prefWidthProperty().bind(productEntryTable.widthProperty().multiply(0.40));
        colQuantity.prefWidthProperty().bind(productEntryTable.widthProperty().multiply(0.20));
        colAction.prefWidthProperty().bind(productEntryTable.widthProperty().multiply(0.08));


        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));
        colProductName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));
        colQuantity.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        colQuantity.setCellFactory(column ->
                new TextFieldTableCell<>(new IntegerStringConverter()) {
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null) {
                            UnaryOperator<TextFormatter.Change> filter = change -> {
                                String newText = change.getControlNewText();
                                return newText.matches("([1-9][0-9]?)?") ? change : null;
                            };
                            textField.setTextFormatter(new TextFormatter<>(filter));
                        }
                    }
                });


        colQuantity.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setQuantity(event.getNewValue());

            total = 0;
            for (Product p : productList) {
                total += p.getQuantity();
            }

            lblTotal.setText(total.toString());
            productEntryTable.refresh();
        });

        productEntryTable.setEditable(true);
        colQuantity.setEditable(true);
        productEntryTable.setItems(productList);
        ProgramHelpers.adjustTableHeight(productEntryTable);

        addDeleteButtonToTable();
    }


    private void addDeleteButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete?");
                    alert.setHeaderText("Are you sure to delete this from the table?");
                    alert.setContentText(product.getProductName() + " (" + product.getQuantity() + ")");

                    ButtonType yes = new ButtonType("Yes");
                    ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(yes, no);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == yes) {
                            total -= product.getQuantity();
                            lblTotal.setText(total.toString());
                            productList.remove(product);
                            ProgramHelpers.adjustTableHeight(productEntryTable);

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
    private void onAddProduct() {
        if (!(quantityField.getText().equals("0") || quantityField.getText().equals("00"))) {
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
                            p.setQuantity(p.getQuantity() + Integer.parseInt(quantityField.getText()));
                            total += Integer.parseInt(quantityField.getText());
                            found = true;

                            barcodeField.clear();
                            break;
                        }
                    }
                    if (!found) {
                        Product newProduct = _repository.getProductModelByBarcode(scannedBarcode);
                        newProduct.setQuantity(Integer.parseInt(quantityField.getText()));
                        productList.add(newProduct);
                        total += newProduct.getQuantity();
                    }


                    lblTotal.setText(total.toString());

                    barcodeField.clear();
                    productEntryTable.refresh();
                    quantityField.setText("1");
                    barcodeField.requestFocus();
                    ProgramHelpers.adjustTableHeight(productEntryTable);

                }

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText("Failed");
                alert.setContentText("Fill the fields, please!");
                alert.showAndWait();
            }
        }

    }

    @FXML
    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/stockandinventory.fxml", btnBack);
    }

    @FXML
    private void cancelProcess() {
        if (productEntryTable.getItems() != null && !productEntryTable.getItems().isEmpty()) {
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
                    lblTotal.setText(total.toString());
                    productEntryTable.refresh();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No record");
            alert.setContentText("You haven't added a product to sale!");
            alert.showAndWait();
        }
        ProgramHelpers.adjustTableHeight(productEntryTable);

    }

    @FXML
    private void addToInventory() {
        String date = ProgramHelpers.getStringDateTimeByLocalDateTime(LocalDateTime.now());
        if (productEntryTable.getItems() != null && !productEntryTable.getItems().isEmpty()) {
            try {
                for (Product p : productList) {
                    _repository.updateStockQuantity(p, true);
                    for (int i = 1; i <= p.getQuantity(); i++) {
                        _repository.insertIntoStockMovementTable(_repository.getLatestMovementId(), "ENTRY", p, "000000", user.getId(), date);
                    }
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setHeaderText("Operation completed successfully");
                alert.setContentText("Products added to the inventory!");
                alert.showAndWait();
                total = 0;
                lblTotal.setText(total.toString());
                barcodeField.clear();
                productList.removeAll();
                productEntryTable.getItems().clear();
                productEntryTable.refresh();
            } catch (Exception e) {
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
        ProgramHelpers.adjustTableHeight(productEntryTable);
    }


}
