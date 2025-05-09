package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Coupon;
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

public class EditCouponModalController implements Initializable {
    Repository _repository = new Repository();
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    private Coupon coupon;
    @FXML
    private TextField discountAmountField;
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
    private TextField couponCodeField;
    @FXML
    private Label lblDiscountFor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        couponCodeField.setEditable(false);
        discountAmountField.setEditable(false);

    }

    public void setCoupon(Coupon coupon) {
        if (coupon != null) {
            this.coupon = coupon;
            LocalDate startDate = LocalDate.parse(coupon.getStartDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            LocalDate endDate = LocalDate.parse(coupon.getEndDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            Boolean isActive = false;

            isActive = coupon.getIsActive().equals("ACTIVE");


            couponCodeField.setText(String.valueOf(coupon.getCouponCode()));
            discountAmountField.setText(String.valueOf(coupon.getDiscountAmount()));
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
            if (!discountAmountField.getText().isEmpty() &&
                    startDatePicker.getValue() != null &&
                    endDatePicker.getValue() != null) {
                LocalDate selectedStartDate = startDatePicker.getValue();
                LocalDate selectedEndDate = endDatePicker.getValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                String strIsActive;
                if (isActiveCheckBox.isSelected()) {
                    strIsActive = "ACTIVE";
                } else {
                    strIsActive = "INACTIVE";
                }
                Coupon coupon = new Coupon(
                        couponCodeField.getText(), discountAmountField.getText(), selectedStartDate.format(formatter), selectedEndDate.format(formatter), strIsActive);
                try {
                    _repository.updateCouponTable(coupon);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Completed");
                    alert.setHeaderText("Saved");
                    alert.setContentText("Coupon saved!");
                    alert.showAndWait();
                    closeModal();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Not Saved");
                    alert.setContentText("An error occured while saving this coupon!");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeModal() {
        Stage stage = (Stage) couponCodeField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void delete() {
        try {
            _repository.deleteFromCouponbyCode(couponCodeField.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Completed");
            alert.setHeaderText("Deleted");
            alert.setContentText("Coupon deleted!");
            alert.showAndWait();
            closeModal();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Not Deleted");
            alert.setContentText("An error occured while deleting this Coupon!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }


}
