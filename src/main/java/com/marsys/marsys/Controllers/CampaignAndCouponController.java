package com.marsys.marsys.Controllers;


import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class CampaignAndCouponController implements Initializable {
    LayoutController _layoutController = new LayoutController();
    @FXML
    private Button btnBack;
    @FXML
    private Button btnGoToCampaignScreen;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserId;
    @FXML
    private Button btnGoToCouponScreen;

    Employee user = Session.getInstance().getCurrentUser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());
    }

    public void goToCampaignScreen() {

        _layoutController.loadPageByButton("/com/marsys/marsys/Views/campaignlist.fxml", btnGoToCampaignScreen);

    }


    public void goToCouponScreen() {

        _layoutController.loadPageByButton("/com/marsys/marsys/Views/couponlist.fxml", btnGoToCouponScreen);

    }

    @FXML
    public void back() {
        _layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }

}