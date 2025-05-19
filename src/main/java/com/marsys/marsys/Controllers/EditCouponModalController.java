package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Coupon;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditCouponModalController implements Initializable {
    Repository _repository = new Repository();
    Employee user = Session.getInstance().getCurrentUser();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private TextField discountAmountField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private CheckBox isActiveCheckBox;
    @FXML
    private TextField couponCodeField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        couponCodeField.setEditable(false);
        discountAmountField.setEditable(false);

    }

    public void setCoupon(Coupon coupon) {
        if (coupon != null) {
            LocalDate startDate = LocalDate.parse(coupon.getStartDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            LocalDate endDate = LocalDate.parse(coupon.getEndDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            boolean isActive = coupon.getIsActive().equals("ACTIVE");


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
            String used = _repository.getCouponUsed(couponCodeField.getText());
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
                        couponCodeField.getText(), discountAmountField.getText(), selectedStartDate.format(formatter), selectedEndDate.format(formatter), strIsActive, used);
                try {
                    _repository.updateCouponTable(coupon);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Completed");
                    alert.setHeaderText("Saved");
                    alert.setContentText("Coupon saved!");
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }


}
