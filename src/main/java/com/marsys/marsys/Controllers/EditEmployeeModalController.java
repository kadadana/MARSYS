package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Repository.BeratRepo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.marsys.marsys.Models.Session;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditEmployeeModalController implements Initializable {

    BeratRepo _repository = new BeratRepo();
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();
    private Employee employee;


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
    public DatePicker endDatePicker;
    @FXML
    public DatePicker birthDatePicker;
    @FXML
    public TextField passwordField;
    @FXML
    private ComboBox<String> storeCodeComboBox;
    @FXML
    private ComboBox<String> positionComboBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
        employeeId.setEditable(false);
        birthDatePicker.setDisable(true);
        birthDatePicker.setEditable(false);
        birthDatePicker.getEditor().setDisable(true);
        startDatePicker.setDisable(true);
        startDatePicker.setEditable(false);
        startDatePicker.getEditor().setDisable(true);
        endDatePicker.setOnAction(event -> {
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
    }


    @FXML
    public void back() {
        closeModal();
    }

    public void setEmployee(Employee employee) {
        if (employee != null) {
            this.employee = employee;
            LocalDate startDate = LocalDate.parse(employee.getStartDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            if (employee.getEndDate() != null && !employee.getEndDate().equals("-")) {
                LocalDate endDate = LocalDate.parse(employee.getEndDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                endDatePicker.setValue(endDate);
            }
            LocalDate birthDate = LocalDate.parse(employee.getBirthDate(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));

            employeeId.setText(String.valueOf(employee.getId()));
            employeeFirstName.setText(String.valueOf(employee.getFirstName()));
            employeeLastName.setText(String.valueOf(employee.getLastName()));
            positionComboBox.setValue(employee.getPosition());
            passwordField.setText(employee.getPassword());
            storeCodeComboBox.setValue(employee.getStoreCode());
            startDatePicker.setValue(startDate);
            birthDatePicker.setValue(birthDate);
        }
    }

    @FXML
    private void delete() {
        try {
            _repository.deleteFromEmployeeById(employeeId.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Completed");
            alert.setHeaderText("Deleted");
            alert.setContentText("Employee deleted!");
            alert.showAndWait();
            closeModal();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Not Deleted");
            alert.setContentText("An error occured while deleting this Employee!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void closeModal() {
        Stage stage = (Stage) employeeId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void save() {
        try {
            if (startDatePicker.getValue() != null &&
                    storeCodeComboBox.getValue() != null) {
                String formattedDate = "-";
                LocalDate selectedStartDate = startDatePicker.getValue();
                LocalDate selectedEndDate = endDatePicker.getValue();
                LocalDate selectedBirthDate = birthDatePicker.getValue();
                if (selectedEndDate != null) {
                    formattedDate = selectedEndDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                }
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
                        selectedBirthDate.format(formatter));
                try {
                    _repository.updateEmployeeTable(employee);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Completed");
                    alert.setHeaderText("Saved");
                    alert.setContentText("Employee saved!");
                    alert.showAndWait();
                    closeModal();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Not Saved");
                    alert.setContentText("An error occured while saving this employee!");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
