package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscountAndCampaignController implements Initializable {
    LayoutController _layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();


    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
    @FXML
    private Button btnGoToCampaignList;
    @FXML
    private Button btnGoToCreateCampaign;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
    }

    public void goToCreateCampaignScreen() {
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/createcampaign.fxml", btnGoToCreateCampaign);
    }

    public void goToCampaignList() {
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/campaignlist.fxml", btnGoToCampaignList);
    }

    public void back() {
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }


}
