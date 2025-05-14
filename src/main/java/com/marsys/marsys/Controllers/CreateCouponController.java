package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Coupon;
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

public class CreateCouponController implements Initializable {
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();
    Repository repository = new Repository();
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        couponCodeField.setEditable(false);
        couponCodeField.setText(repository.getLatestCouponCode());
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


    }

    public void save() {

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
                    repository.getLatestCouponCode(), discountAmountField.getText(), selectedStartDate.format(formatter), selectedEndDate.format(formatter), strIsActive, "0");
            try {
                repository.insertIntoCouponTable(coupon);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setHeaderText("Saved");
                alert.setContentText("Coupon added!");
                alert.showAndWait();
                layoutController.loadPageByButton("/com/marsys/marsys/Views/couponlist.fxml", btnSave);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Not Saved");
                alert.setContentText("An error occured while saving this coupon!");
                alert.showAndWait();
                e.printStackTrace();
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

        layoutController.loadPageByButton("/com/marsys/marsys/Views/couponlist.fxml", btnBack);


    }

}
