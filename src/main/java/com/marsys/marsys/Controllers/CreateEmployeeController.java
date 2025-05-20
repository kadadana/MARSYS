package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Coupon;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.BeratRepo;
import com.marsys.marsys.Repository.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CreateEmployeeController implements Initializable {
    BeratRepo _repository = new BeratRepo();
    Repository repository = new Repository();
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();

    @FXML
    public Label lblUserId;
    @FXML
    public Label lblUserName;
    @FXML
    public TextField employeeId;
    @FXML
    public TextField employeeFirstName;
    @FXML
    public TextField employeeLastName;
    @FXML
    public DatePicker startDatePicker;
    @FXML
    public DatePicker birthDatePicker;
    @FXML
    public TextField passwordField;
    @FXML
    private ComboBox<String> storeCodeComboBox;
    @FXML
    private ComboBox<String> positionComboBox;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        employeeId.setEditable(false);
        employeeId.setText(_repository.getLatestEmployeeId());
        birthDatePicker.getEditor().setDisable(true);
        startDatePicker.getEditor().setDisable(true);
        startDatePicker.setEditable(false);
        startDatePicker.setDisable(true);
        startDatePicker.setValue(LocalDate.now().plusDays(1));
        birthDatePicker.setOnAction(event -> {
            if (birthDatePicker.getValue() != null) {
                if (birthDatePicker.getValue().isAfter(LocalDate.now().minusYears(15))) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Date Error");
                    alert.setContentText("People younger than 15 years old can not work this company!");
                    alert.showAndWait();
                    birthDatePicker.setValue(null);
                }
            }
        });

    }

    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/employeemanagement.fxml", btnBack);
    }

    public void save() {
        try {
            if (startDatePicker.getValue() != null &&
                    storeCodeComboBox.getValue() != null &&
                    !employeeId.getText().isBlank() &&
                    !employeeFirstName.getText().isBlank() &&
                    !employeeLastName.getText().isBlank() &&
                    positionComboBox.getValue() != null &&
                    birthDatePicker.getValue() != null &&
                    !passwordField.getText().isBlank()) {

                String formattedDate = "-";
                LocalDate selectedStartDate = startDatePicker.getValue();
                LocalDate selectedBirthDate = birthDatePicker.getValue();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");


                Employee employee = new Employee(
                        employeeFirstName.getText(),
                        employeeLastName.getText(),
                        positionComboBox.getValue(),
                        employeeId.getText(),
                        passwordField.getText(),
                        storeCodeComboBox.getValue(),
                        selectedStartDate.format(formatter),
                        formattedDate,
                        selectedBirthDate.format(formatter),
                        repository.getLatestCouponCode());
                Coupon employeeCoupon = new Coupon(repository.getLatestCouponCode(),
                        "1000",
                        selectedStartDate.format(formatter),
                        "12-31-9999",
                        "ACTIVE",
                        "0",
                        "1");
                try {
                    _repository.insertIntoEmployeeTable(employee);
                    repository.insertIntoCouponTable(employeeCoupon);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Completed");
                    alert.setHeaderText("Saved");
                    alert.setContentText("Employee created!");
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Program Error");
                    alert.setHeaderText("An error occured in this operation.");
                    alert.setContentText(e.toString());
                    alert.showAndWait();
                }
                layoutController.loadPageByButton("/com/marsys/marsys/Views/employeemanagement.fxml", btnBack);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Empty field");
                alert.setContentText("All fields must be filled!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }
}