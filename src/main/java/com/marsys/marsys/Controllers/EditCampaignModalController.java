package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditCampaignModalController implements Initializable {
    Repository _repository = new Repository();
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    private Campaign campaign;
    @FXML
    private ComboBox<String> discountTypeComboBox;
    @FXML
    private TextField discountValueField;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        campaignId.setEditable(false);
        discountTypeComboBox.setDisable(true);
        discountTypeComboBox.setEditable(false);
        discountValueField.setEditable(false);

    }

    public void setCampaign(Campaign campaign) {
        if (campaign != null) {
            this.campaign = campaign;
            LocalDate startDate = LocalDate.parse(campaign.getStartDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            LocalDate endDate = LocalDate.parse(campaign.getEndDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            Boolean isActive = false;

            isActive = campaign.getIsActive().equals("ACTIVE");


            campaignId.setText(String.valueOf(campaign.getCampaignId()));
            discountTypeComboBox.setValue(campaign.getDiscountType());
            discountValueField.setText(String.valueOf(campaign.getDiscountValue()));
            startDatePicker.setValue(startDate);
            endDatePicker.setValue(endDate);
            isActiveCheckBox.setSelected(isActive);
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
                    !discountValueField.getText().isEmpty() &&
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
                    case "Coupon Code":
                        discountTypeCode = "05";
                        break;
                    default:
                        break;
                }

                if (isActiveCheckBox.isSelected()) {
                    strIsActive = "ACTIVE";
                } else {
                    strIsActive = "INACTIVE";
                }
                Campaign campaign = new Campaign(
                        campaignId.getText(), discountTypeComboBox.getSelectionModel().getSelectedItem(), discountTypeCode,
                        discountValueField.getText(), selectedStartDate.format(formatter), selectedEndDate.format(formatter), strIsActive);
                try {
                    _repository.updateCampaignTable(campaign);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Completed");
                    alert.setHeaderText("Saved");
                    alert.setContentText("Campaign saved!");
                    alert.showAndWait();
                    closeModal();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Not Saved");
                    alert.setContentText("An error occured while saving this campaign!");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeModal() {
        Stage stage = (Stage) campaignId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void delete() {
        try {
            _repository.deleteFromCampaignById(campaignId.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Completed");
            alert.setHeaderText("Deleted");
            alert.setContentText("Campaign deleted!");
            alert.showAndWait();
            closeModal();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Not Deleted");
            alert.setContentText("An error occured while deleting this campaign!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }


}
