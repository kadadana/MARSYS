package com.marsys.marsys.Controllers;

import com.marsys.marsys.Helpers.ProgramHelpers;
import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditCampaignModalController implements Initializable {
    Repository _repository = new Repository();
    Employee user = Session.getInstance().getCurrentUser();
    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
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
    private TextField campaignId;
    @FXML
    private Label lblDiscountFor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        campaignId.setEditable(false);
        discountTypeComboBox.setDisable(true);
        discountTypeComboBox.setEditable(false);


    }

    public void setCampaign(Campaign campaign) {
        if (campaign != null) {

            boolean isActive = campaign.getIsActive().equals("ACTIVE");

            campaignId.setText(String.valueOf(campaign.getCampaignId()));
            discountTypeComboBox.setValue(campaign.getDiscountType());
            barcodeField.setText(String.valueOf(campaign.getDiscountFor()));
            startDatePicker.setValue(ProgramHelpers.getLocalDateByStringDate(campaign.getStartDate()));
            endDatePicker.setValue(ProgramHelpers.getLocalDateByStringDate(campaign.getEndDate()));
            isActiveCheckBox.setSelected(isActive);
            categoryComboBox.setValue(campaign.getDiscountFor());
            if (discountTypeComboBox.getValue().equals("50% Discount for 2nd from the same category")) {
                lblDiscountFor.setText("Category: ");
                barcodeField.setVisible(false);
                barcodeField.setManaged(false);
                categoryComboBox.setVisible(true);
                categoryComboBox.setManaged(true);
                categoryComboBox.setDisable(true);
                categoryComboBox.setEditable(false);
            } else {
                lblDiscountFor.setText("Barcode: ");
                barcodeField.setVisible(true);
                barcodeField.setManaged(true);
                categoryComboBox.setVisible(false);
                categoryComboBox.setManaged(false);
                barcodeField.setEditable(false);


            }
        }
    }

    @FXML
    public void back() {
        closeModal();
    }

    @FXML
    private void save() {
        try {
            if (discountTypeComboBox.getValue() != null &&
                    (!barcodeField.getText().isEmpty() || categoryComboBox.getValue() != null) &&
                    startDatePicker.getValue() != null &&
                    endDatePicker.getValue() != null) {
                LocalDate selectedStartDate = startDatePicker.getValue();
                LocalDate selectedEndDate = endDatePicker.getValue();
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
                            _repository.getLatestCampaignId(),
                            discountTypeComboBox.getSelectionModel().getSelectedItem(),
                            discountTypeCode,
                            categoryComboBox.getSelectionModel().getSelectedItem(),
                            ProgramHelpers.getStringDateByLocalDate(selectedStartDate),
                            ProgramHelpers.getStringDateByLocalDate(selectedEndDate),
                            strIsActive);
                } else {
                    campaign = new Campaign(
                            _repository.getLatestCampaignId(),
                            discountTypeComboBox.getSelectionModel().getSelectedItem(),
                            discountTypeCode,
                            barcodeField.getText(),
                            ProgramHelpers.getStringDateByLocalDate(selectedStartDate),
                            ProgramHelpers.getStringDateByLocalDate(selectedEndDate),
                            strIsActive);
                }


                try {
                    _repository.updateCampaignTable(campaign);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Completed");
                    alert.setHeaderText("Saved");
                    alert.setContentText("Campaign saved!");
                    alert.showAndWait();
                    closeModal();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Program Error");
                    alert.setHeaderText("An error occured in this operation.");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    private void closeModal() {
        Stage stage = (Stage) campaignId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void delete() {
        if (!discountTypeComboBox.getValue().equals("%25 Discount for Cash Payment")) {
            try {
                _repository.deleteFromCampaignById(campaignId.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setHeaderText("Deleted");
                alert.setContentText("Campaign deleted!");
                alert.showAndWait();
                closeModal();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Program Error");
                alert.setHeaderText("An error occured in this operation.");
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Not Deleted");
            alert.setContentText("You can't delete this campaign. You can only change it's activity!");
            alert.showAndWait();
        }

    }


}
