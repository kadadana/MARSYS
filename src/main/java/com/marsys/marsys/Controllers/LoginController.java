package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import eu.hansolo.toolbox.unit.Converter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    Repository _repository = new Repository();

    @FXML
    private TextField storeCodeField;
    @FXML
    private TextField idField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {

        String enteredStoreCode = storeCodeField.getText();
        String enteredId = idField.getText();
        String enteredPassword = passwordField.getText();
        String password = _repository.getCellById("EMPLOYEE", "PASSWORD", enteredId);
        String storeCode = _repository.getCellById("EMPLOYEE","STORE_CODE",enteredId);


        if (enteredPassword.equals(password) && enteredStoreCode.equals(storeCode)) {
            String id = _repository.getCellById("EMPLOYEE", "ID", enteredId);
            String name = _repository.getCellById("EMPLOYEE", "NAME", enteredId);
            String lastName = _repository.getCellById("EMPLOYEE", "LAST_NAME", enteredId);
            String position = _repository.getCellById("EMPLOYEE", "POSITION", enteredId);
            Employee loggedInEmployee = new Employee(name, lastName, position,id, password, storeCode);
            Session.getInstance().setCurrentUser(loggedInEmployee);
            showNextScene();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Log in failed");
            alert.setHeaderText("Invalid Username, Password or Store Code!");
            alert.setContentText("Please check your username, password and store code.");
            alert.showAndWait();
        }
    }

    private void showNextScene() {
        Stage stage = (Stage) idField.getScene().getWindow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/mainpage.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 720, 720);
            stage.setScene(newScene);
            stage.setTitle("Main Page");
        } catch (IOException e) {
            showErrorMessage("Error Loading Next Scene", "There was an error loading the next screen. Please try again.");
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String headerText, String contentText) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Log in failed");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}