package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Coupon;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CouponListController implements Initializable {
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private TableView<Coupon> couponTable;
    @FXML
    private TableColumn<Coupon, String> colCouponCode;
    @FXML
    private TableColumn<Coupon, String> colDiscountAmount;
    @FXML
    private TableColumn<Coupon, String> colStartDate;
    @FXML
    private TableColumn<Coupon, String> colEndDate;
    @FXML
    private TableColumn<Coupon, String> colIsActive;
    @FXML
    private TableColumn<Coupon, Void> colEdit;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnGoToCreateCouponScreen;


    private ObservableList<Coupon> couponList = FXCollections.observableArrayList();
    Repository _repository = new Repository();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());

        couponList.addAll(_repository.getCouponList());

        colCouponCode.prefWidthProperty().bind(couponTable.widthProperty().multiply(0.2));
        colDiscountAmount.prefWidthProperty().bind(couponTable.widthProperty().multiply(0.2));
        colStartDate.prefWidthProperty().bind(couponTable.widthProperty().multiply(0.2));
        colEndDate.prefWidthProperty().bind(couponTable.widthProperty().multiply(0.2));
        colIsActive.prefWidthProperty().bind(couponTable.widthProperty().multiply(0.13));
        colEdit.prefWidthProperty().bind(couponTable.widthProperty().multiply(0.05));

        colCouponCode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCouponCode()));
        colDiscountAmount.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiscountAmount()));
        colStartDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDate()));
        colEndDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDate()));
        colIsActive.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIsActive()));

        couponTable.setItems(couponList);
        addEditButtonToTable();
    }

    private void addEditButtonToTable() {
        colEdit.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Coupon coupon = _repository.getCouponModelByCode(getTableView().getItems().get(getIndex()).getCouponCode());
                    openEditWindow(coupon, editButton);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    public void goToCreateCouponScreen() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/createcoupon.fxml", btnGoToCreateCouponScreen);

    }

    private void openEditWindow(Coupon coupon, Button btnEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/editcouponmodal.fxml"));
            Parent root = loader.load();
            EditCouponModalController controller = loader.getController();
            controller.setCoupon(coupon);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshCouponTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/campaignandcoupon.fxml", btnBack);
    }

    private void refreshCouponTable() {
        couponList.clear();
        couponList.addAll(_repository.getCouponList());
        couponTable.setItems(couponList);
    }
}
