package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ReturnsController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private Button btnBack;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());

    }
    @FXML
    private void getInvoice(){

    }
    @FXML
    private void back() {
        LayoutController _layoutController = new LayoutController();
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }
    @FXML
    private void cancelProcess() {


    }
    @FXML
    private void completeReturn(){

    }
}
