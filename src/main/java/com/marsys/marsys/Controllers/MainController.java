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
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    Employee user = Session.getInstance().getCurrentUser();

    public void goToSalesScreen(ActionEvent event) {
        if(user.getPosition().equals("MANAGER")){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/sales.fxml"));
                Parent salesRoot = loader.load();

                Stage stage  = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene salesScene  = new Scene(salesRoot);

                stage.setScene(salesScene);
                stage.show();
            }catch (IOException e){
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to load the Sales screen.");
                alert.showAndWait();
            }


        }else {
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
    public void exitApp() {
        // Burada sales screen'e gitme işlemini yapabilirsiniz.
    }

}