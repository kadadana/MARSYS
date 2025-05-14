package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import eu.hansolo.toolbox.unit.Converter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    Repository _repository = new Repository();
    LayoutController _layoutController = new LayoutController();


    @FXML
    private TextField storeCodeField;
    @FXML
    private TextField idField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button btnLogin;

    @FXML
    private void initialize() {
        storeCodeField.setOnAction(event -> handleLogin());
        idField.setOnAction(event -> handleLogin());
        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    public void handleLogin() {
        if (!storeCodeField.getText().isBlank() && !idField.getText().isBlank()) {
            String enteredStoreCode = storeCodeField.getText();
            String enteredId = idField.getText();
            String enteredPassword = passwordField.getText();
            Employee employee = _repository.getEmployeeModelById(enteredId);


            if (employee != null) {
                if (enteredPassword.equals(employee.getPassword()) && enteredStoreCode.equals(employee.getStoreCode())) {

                    Session.getInstance().setCurrentUser(employee);
                    _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnLogin);


                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Log in failed");
                    alert.setHeaderText("Invalid Username, Password or Store Code!");
                    alert.setContentText("Please check your username, password and store code.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Log in failed");
                alert.setHeaderText("Invalid Username, Password or Store Code!");
                alert.setContentText("Please check your username, password and store code.");
                alert.showAndWait();
            }


        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Log in failed");
            alert.setHeaderText("Failed!");
            alert.setContentText("Fill the field, please!");
            alert.showAndWait();

        }


    }


}