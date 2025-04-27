package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Product;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class CampaignListController implements Initializable {
    LayoutController layoutController = new LayoutController();
    Employee user = Session.getInstance().getCurrentUser();

    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private TableView<Campaign> campaignTable;
    @FXML
    private TableColumn<Campaign, String> colCampaignId;
    @FXML
    private TableColumn<Campaign, String> colDiscountType;
    @FXML
    private TableColumn<Campaign, String> colDiscountValue;
    @FXML
    private TableColumn<Campaign, String> colStartDate;
    @FXML
    private TableColumn<Campaign, String> colEndDate;
    @FXML
    private TableColumn<Campaign, String> colIsActive;
    @FXML
    private TableColumn<Campaign, Void> colEdit;
    @FXML
    private Button btnBack;


    private ObservableList<Campaign> campaignList = FXCollections.observableArrayList();
    Repository _repository = new Repository();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
        lblUserId.setText("ID: " + user.getId());

        campaignList.addAll(_repository.getAllCampaigns());

        colCampaignId.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.1));
        colDiscountType.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.3));
        colDiscountValue.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.2));
        colStartDate.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.1));
        colEndDate.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.1));
        colIsActive.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.13));
        colEdit.prefWidthProperty().bind(campaignTable.widthProperty().multiply(0.05));

        colCampaignId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCampaignId()));
        colDiscountType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiscountType()));
        colDiscountValue.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiscountValue()));
        colStartDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDate()));
        colEndDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDate()));
        colIsActive.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIsActive()));

        campaignTable.setItems(campaignList);
        addEditButtonToTable();
    }

    private void addEditButtonToTable() {
        colEdit.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Campaign campaign = getTableView().getItems().get(getIndex());

                    openEditWindow(campaign, editButton);
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

    private void openEditWindow(Campaign campaign, Button btnEdit) {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/createcampaign.fxml", btnEdit);
    }

    public void back() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }
}
