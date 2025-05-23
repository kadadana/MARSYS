package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Models.StockMovement;
import com.marsys.marsys.Repository.Repository;
import com.marsys.marsys.Repository.RepositoryMete;
import javafx.beans.property.SimpleObjectProperty;
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
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import com.marsys.marsys.Helpers.ProgramHelpers;
import org.jetbrains.annotations.NotNull;

public class StockMovementTableController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();
    LayoutController layoutController = new LayoutController();
    RepositoryMete repositoryMete = new RepositoryMete();
    Repository repository = new Repository();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private Button btnClear;
    @FXML
    private TableColumn<StockMovement, Void> colProductInfo;
    @FXML
    private TableColumn<StockMovement, Void> colInvoiceInfo;
    @FXML
    private TableView<StockMovement> stockMovementTable;
    @FXML
    private TableColumn<StockMovement, String> colBarcode;
    @FXML
    private TableColumn<StockMovement, String> colMovementId;
    @FXML
    private TableColumn<StockMovement, String> colMovementType;
    @FXML
    private TableColumn<StockMovement, String> colInvoiceNumber;
    @FXML
    private TableColumn<StockMovement, String> colUser;
    @FXML
    private TableColumn<StockMovement, LocalDateTime> colDate;
    @FXML
    private Button btnBack;
    @FXML
    private TextField barcodeField;
    @FXML
    private TextField invoiceNumberField;
    @FXML
    private DatePicker firstDatePicker;
    @FXML
    private DatePicker lastDatePicker;


    private final ObservableList<StockMovement> stockMovementList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        barcodeField.setOnAction(event -> search());
        invoiceNumberField.setOnAction(event -> search());

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
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

        invoiceNumberField.setTextFormatter(textFormatter);
        barcodeField.setTextFormatter(textFormatter2);
        btnClear.setVisible(false);
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        stockMovementList.addAll(repositoryMete.getStockMovementList());
        firstDatePicker.getEditor().setDisable(true);
        lastDatePicker.getEditor().setDisable(true);
        colMovementId.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.1));
        colBarcode.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.1));
        colMovementType.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.1));
        colInvoiceNumber.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.2));
        colUser.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.1));
        colDate.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.18));
        colProductInfo.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.1));
        colInvoiceInfo.prefWidthProperty().bind(stockMovementTable.widthProperty().multiply(0.1));


        colMovementId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMovementId()));
        colMovementType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMovementType()));
        colBarcode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBarcode()));
        colInvoiceNumber.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getInvoiceNumber()));
        colUser.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser()));
        colDate.setCellValueFactory(cellData -> {
            try {
                String dateStr = cellData.getValue().getDate();
                LocalDateTime ldt = ProgramHelpers.getLocalDateTimeTimeByStringDateTime(dateStr);

                return new SimpleObjectProperty<>(ldt);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Program Error");
                alert.setHeaderText("An error occured in this operation.");
                alert.setContentText(e.toString());
                alert.showAndWait();
                return new SimpleObjectProperty<>(null);
            }
        });
        colDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(ProgramHelpers.getStringDateTimeByLocalDateTime(item));
                }
            }
        });


        stockMovementTable.setItems(stockMovementList);
        stockMovementTable.getItems().addListener((ListChangeListener<StockMovement>) c -> ProgramHelpers.adjustTableHeight(stockMovementTable));
        addProductInfoButtonsToTable();
        addInvoiceInfoButtonsToTable();

        ProgramHelpers.adjustTableHeight(stockMovementTable);
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
        String invoiceNumberText = invoiceNumberField.getText();
        if (invoiceNumberText == null) {
            invoiceNumberText = "";
        }
        LocalDate firstDate = firstDatePicker.getValue();
        LocalDate lastDate = lastDatePicker.getValue();
        String strFirstDate = null;
        String strLastDate = null;

        if (firstDate != null && lastDate != null) {
            strFirstDate = ProgramHelpers.getStringDateByLocalDate(firstDate);
            strLastDate = ProgramHelpers.getStringDateByLocalDate(lastDate);
        }
        boolean datePicked = firstDatePicker.getValue() != null && lastDatePicker.getValue() != null;
        stockMovementList.clear();

        String code = getSearchingCode(datePicked, barcodeText, invoiceNumberText);

        stockMovementList.addAll(repositoryMete.getStockMovementListBySearching(
                code,
                strFirstDate,
                strLastDate,
                barcodeText,
                invoiceNumberText
        ));


        btnClear.setVisible(true);
        stockMovementTable.refresh();

        ProgramHelpers.adjustTableHeight(stockMovementTable);

    }

    @NotNull
    private static String getSearchingCode(boolean datePicked, String barcodeText, String invoiceNumberText) {
        String code;
        if (datePicked) {
            if (!barcodeText.isBlank() && !invoiceNumberText.isBlank()) code = "01";
            else if (!barcodeText.isBlank() && invoiceNumberText.isBlank()) code = "02";
            else if (barcodeText.isBlank() && !invoiceNumberText.isBlank()) code = "03";
            else code = "04";
        } else {
            if (!barcodeText.isBlank() && !invoiceNumberText.isBlank()) code = "05";
            else if (!barcodeText.isBlank() && invoiceNumberText.isBlank()) code = "06";
            else if (barcodeText.isBlank() && !invoiceNumberText.isBlank()) code = "07";
            else code = "08";
        }
        return code;
    }

    @FXML
    private void clearSearch() {
        firstDatePicker.setValue(null);
        lastDatePicker.setValue(null);
        barcodeField.setText(null);
        invoiceNumberField.setText(null);
        btnClear.setVisible(false);
        stockMovementList.clear();
        stockMovementList.addAll(repositoryMete.getStockMovementList());
        stockMovementTable.refresh();

        ProgramHelpers.adjustTableHeight(stockMovementTable);

    }

    private void addProductInfoButtonsToTable() {
        colProductInfo.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Product Details");

            {
                btn.setOnAction(event -> {
                    StockMovement movement = getTableView().getItems().get(getIndex());
                    openProductInfoModal(movement.getBarcode());
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

    private void addInvoiceInfoButtonsToTable() {
        colInvoiceInfo.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Invoice Details");

            {
                btn.setOnAction(event -> {
                    StockMovement movement = getTableView().getItems().get(getIndex());
                    openInvoiceInfoModal(movement.getInvoiceNumber());
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

    private void openProductInfoModal(String barcode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/productdetails.fxml"));
            Parent root = loader.load();


            ProductDetailsController controller = loader.getController();
            controller.setProduct(repository.getProductModelByBarcode(barcode));

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

    private void openInvoiceInfoModal(String invoiceNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/invoicedetails.fxml"));
            Parent root = loader.load();


            InvoiceDetailsController controller = loader.getController();
            controller.setInvoice(repository.getInvoiceModelByInvoiceNumber(invoiceNumber));

            Stage stage = new Stage();
            stage.setWidth(900);
            stage.setHeight(600);
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


}
