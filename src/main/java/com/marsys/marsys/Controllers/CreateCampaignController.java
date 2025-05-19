package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CreateCampaignController implements Initializable {
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();
    Repository repository = new Repository();
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
    @FXML
    private ComboBox<String> discountTypeComboBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField barcodeField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private CheckBox isActiveCheckBox;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnSave;
    @FXML
    private TextField campaignId;
    @FXML
    private Label lblDiscountFor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        campaignId.setEditable(false);
        campaignId.setText(repository.getLatestCampaignId());
        startDatePicker.getEditor().setDisable(true);
        endDatePicker.getEditor().setDisable(true);
        startDatePicker.setOnAction(event -> {
            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Date Error");
                    alert.setContentText("Start date cannot be later than end date!");
                    alert.showAndWait();
                    startDatePicker.setValue(endDatePicker.getValue());
                }
            }
        });

        endDatePicker.setOnAction(event -> {
            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Date Error");
                    alert.setContentText("End date cannot be earlier than start date!");
                    alert.showAndWait();
                    endDatePicker.setValue(startDatePicker.getValue());
                }
            }
        });

        discountTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("50% Discount for 2nd from the same category".equals(newVal)) {
                lblDiscountFor.setText("Category: ");
                barcodeField.setVisible(false);
                barcodeField.setManaged(false);
                categoryComboBox.setVisible(true);
                categoryComboBox.setManaged(true);

            } else {
                lblDiscountFor.setText("Barcode: ");
                barcodeField.setVisible(true);
                barcodeField.setManaged(true);
                categoryComboBox.setVisible(false);
                categoryComboBox.setManaged(false);
            }
        });


    }

    public void save() {

        if (discountTypeComboBox.getValue() != null &&
                (!barcodeField.getText().isEmpty() || categoryComboBox.getValue() != null) &&
                startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null) {
            LocalDate selectedStartDate = startDatePicker.getValue();
            LocalDate selectedEndDate = endDatePicker.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String strIsActive;
            String discountTypeCode = "00";

            switch (discountTypeComboBox.getSelectionModel().getSelectedItem()) {
                case "Buy 2 Pay 1":
                    discountTypeCode = "01";
                    break;
                case "50% Discount for 2nd from the same product":
                    discountTypeCode = "02";
                    break;
                case "50% Discount for 2nd from the same category":
                    discountTypeCode = "03";
                    break;
                case "%25 Discount for Cash Payment":
                    discountTypeCode = "04";
                    break;
                default:
                    break;
            }

            if (isActiveCheckBox.isSelected()) {
                strIsActive = "ACTIVE";
            } else {
                strIsActive = "INACTIVE";
            }
            Campaign campaign;
            if (discountTypeCode.equals("03")) {
                campaign = new Campaign(
                        repository.getLatestCampaignId(),
                        discountTypeComboBox.getSelectionModel().getSelectedItem(),
                        discountTypeCode,
                        categoryComboBox.getSelectionModel().getSelectedItem(),
                        selectedStartDate.format(formatter),
                        selectedEndDate.format(formatter),
                        strIsActive);
            } else {
                campaign = new Campaign(
                        repository.getLatestCampaignId(),
                        discountTypeComboBox.getSelectionModel().getSelectedItem(),
                        discountTypeCode,
                        barcodeField.getText(),
                        selectedStartDate.format(formatter),
                        selectedEndDate.format(formatter),
                        strIsActive);
            }

            try {
                repository.insertIntoCampaignTable(campaign);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setHeaderText("Saved");
                alert.setContentText("Campaign added!");
                alert.showAndWait();
                layoutController.loadPageByButton("/com/marsys/marsys/Views/campaignlist.fxml", btnSave);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Program Error");
                alert.setHeaderText("An error occured in this operation.");
                alert.setContentText(e.toString());
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Empty field");
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();
        }
    }

    public void back() {

        layoutController.loadPageByButton("/com/marsys/marsys/Views/campaignlist.fxml", btnBack);


    }

}
