package com.marsys.marsys.Controllers;

import com.almasb.fxgl.entity.action.Action;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    LayoutController _layoutController = new LayoutController();
    @FXML
    private Button btnLogout;
    @FXML
    private Button goToSalesScreen;


    Employee user = Session.getInstance().getCurrentUser();

    public void goToSalesScreen(ActionEvent event) {
        if (user.getPosition().equals("MANAGER") || user.getPosition().equals("CASHIER")) {

            _layoutController.loadPageByButton("/com/marsys/marsys/Views/sales.fxml", goToSalesScreen);


        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You don't have access for this operation!");
            alert.showAndWait();
        }


    }

    public void goToProductEntryScreen() {

    }

    public void goToStockInventoryScreen() {
        // Burada sales screen'e gitme işlemini yapabilirsiniz.
    }

    public void goToSalesReportsScreen() {
        // Burada sales screen'e gitme işlemini yapabilirsiniz.
    }

    public void goToProductManagementScreen() {
        // Burada sales screen'e gitme işlemini yapabilirsiniz.
    }

    public void goToEmployeeManagementScreen() {
        // Burada sales screen'e gitme işlemini yapabilirsiniz.
    }

    public void goToDiscountCampaignScreen() {
        // Burada sales screen'e gitme işlemini yapabilirsiniz.
    }

    public void logout(ActionEvent event) {
        Session.getInstance().setCurrentUser(null);
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/login.fxml", btnLogout);
    }

}