package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.marsys.marsys.Repository.BeratRepo;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
public class EmployeeManagementController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();

    LayoutController layoutController = new LayoutController();
    @FXML
    public Label lblUserId;
    @FXML
    public Label lblUserName;
    @FXML
    public Button btnBack;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    public TableColumn<Employee, String> colId;
    @FXML
    public TableColumn<Employee, String> colName;
    @FXML
    public TableColumn<Employee, String> colLastName;
    @FXML
    public TableColumn<Employee, String> colPosition;
    @FXML
    public TableColumn<Employee, String> colPassword;
    @FXML
    public TableColumn<Employee, String> colStoreCode;
    @FXML
    public TableColumn<Employee, String> colStartDate;
    @FXML
    public TableColumn<Employee, String> colEndDate;
    @FXML
    public TableColumn<Employee, String> colBirthDate;
    @FXML
    public TableColumn<Employee, Void> colEdit;
    @FXML
    public Button btnGoToAddEmployeeScreen;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    BeratRepo _repository = new BeratRepo();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUserId.setText(user.getId());
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());

        employeeList.addAll(_repository.getAllEmployees());


        colId.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colName.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.13));
        colLastName.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colBirthDate.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colPosition.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colPassword.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colStoreCode.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colStartDate.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colEndDate.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.10));
        colEdit.prefWidthProperty().bind(employeeTable.widthProperty().multiply(0.05));

        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName()));
        colLastName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName()));
        colBirthDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBirthDate()));
        colPosition.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPosition()));
        colPassword.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPassword()));
        colStoreCode.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStoreCode()));
        colStartDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDate()));
        colEndDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDate()));

        employeeTable.setItems(employeeList);
        addEditButtonToTable();

    }

    private void addEditButtonToTable() {
        colEdit.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Employee employee = _repository.getEmployeeModelById(getTableView().getItems().get(getIndex()).getId());
                    openEditWindow(employee, editButton);
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

    private void openEditWindow(Employee employee, Button btnEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/editemployeemodal.fxml"));
            Parent root = loader.load();
            EditEmployeeModalController controller = loader.getController();
            controller.setEmployee(employee);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshEmployeeTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshEmployeeTable() {
        employeeList.clear();
        employeeList.addAll(_repository.getAllEmployees());
        employeeTable.setItems(employeeList);
    }

    public void back() {

        layoutController.loadPageByButton("/com/marsys/marsys/Views/mainpage.fxml", btnBack);
    }

    public void goToAddEmployeeScreen() {
        layoutController.loadPageByButton("/com/marsys/marsys/Views/createemployee.fxml", btnGoToAddEmployeeScreen);

    }
}
