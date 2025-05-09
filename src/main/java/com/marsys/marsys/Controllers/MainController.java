package com.marsys.marsys.Controllers;


import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    LayoutController _layoutController = new LayoutController();
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnGoToSalesScreen;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
    @FXML
    private Button btnGoToDiscountCampaignScreen;

    Employee user = Session.getInstance().getCurrentUser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
    }

    public void goToSalesScreen() {
        if (user.getPosition().equals("MANAGER") || user.getPosition().equals("CASHIER")) {

            _layoutController.loadPageByButton("/com/marsys/marsys/Views/sales.fxml", btnGoToSalesScreen);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You don't have access for this operation!");
            alert.showAndWait();
        }
    }


    public void goToStockInventoryScreen() {
        if (user.getPosition().equals("MANAGER") || user.getPosition().equals("CASHIER")) {

            _layoutController.loadPageByButton("/com/marsys/marsys/Views/stockandinventory.fxml", btnGoToSalesScreen);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You don't have access for this operation!");
            alert.showAndWait();
        }
    }

    public void goToSalesReportsScreen() {
        if (user.getPosition().equals("MANAGER") || user.getPosition().equals("CASHIER")) {

            _layoutController.loadPageByButton("/com/marsys/marsys/Views/reports.fxml", btnGoToSalesScreen);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You don't have access for this operation!");
            alert.showAndWait();
        }
    }


    public void goToEmployeeManagementScreen() {
        if (user.getPosition().equals("MANAGER") || user.getPosition().equals("CASHIER")) {

            _layoutController.loadPageByButton("/com/marsys/marsys/Views/employeemanagement.fxml", btnGoToSalesScreen);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You don't have access for this operation!");
            alert.showAndWait();
        }
    }

    public void goToDiscountCampaignScreen() {
        if (user.getPosition().equals("MANAGER")) {

            _layoutController.loadPageByButton("/com/marsys/marsys/Views/campaignandcoupon.fxml", btnGoToDiscountCampaignScreen);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You don't have access for this operation!");
            alert.showAndWait();
        }
    }

    public void logout(ActionEvent event) {
        Session.getInstance().setCurrentUser(null);
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/login.fxml", btnLogout);
    }

}