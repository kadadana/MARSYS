package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Employee;
import com.marsys.marsys.Models.Session;
import com.marsys.marsys.Repository.Repository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentModalController implements Initializable {
    Employee user = Session.getInstance().getCurrentUser();
    Repository _repository = new Repository();
    @FXML
    private Label lblUserId;
    @FXML
    private Label lblUserName;
    @FXML
    private TextField cardNumber;
    @FXML
    private PasswordField cardPassword;
    @FXML
    private Label lblTotal;
    private double paymentTotal;

    private PaymentCompleteListener paymentCompleteListener;

    public void setPaymentTotal(double total) {
        this.paymentTotal = total;
        lblTotal.setText(total + " TL");
    }

    public void setPaymentCompleteListener(PaymentCompleteListener listener) {
        this.paymentCompleteListener = listener;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardNumber.setOnAction(event -> handlePayment());
        cardPassword.setOnAction(event -> handlePayment());
        setPaymentTotal(paymentTotal);
        lblUserId.setText(user.getId());
        lblUserName.setText(user.getFirstName() + " " + user.getLastName());
    }


    @FXML
    private void handlePayment() {
        if(!cardNumber.getText().isBlank()){
            try {
                if (cardNumber.getText().equals(_repository.getCardInfo("NUMBER", "NUMBER", cardNumber.getText())) &&
                        cardPassword.getText().equals(_repository.getCardInfo("PASSWORD", "PASSWORD", cardPassword.getText())) &&
                        (_repository.getCardInfo("BALANCE", "NUMBER", cardNumber.getText())) != null) {
                    if (paymentTotal <= Double.parseDouble(_repository.getCardInfo("BALANCE", "NUMBER", cardNumber.getText()))) {
                        if (paymentCompleteListener != null) {
                            paymentCompleteListener.onPaymentComplete(cardNumber.getText());
                        }
                        _repository.reduceCardBalance(paymentTotal, cardNumber.getText());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText("Completed");
                        alert.setContentText("Sale is completed!");
                        alert.showAndWait();
                        closeModal();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Not Completed");
                        alert.setContentText("The card entered has not enough balance for this transaction!");
                        alert.showAndWait();
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid card");
                    alert.setContentText("Card number or password is incorrect.");
                    alert.showAndWait();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Information");
            alert.setHeaderText("Cancelled");
            alert.setContentText("Fill the fields, please!");
            alert.showAndWait();

        }



    }

    @FXML
    private void handleCancel() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
        alert.setHeaderText("Cancelled");
        alert.setContentText("Payment is cancelled!");
        alert.showAndWait();
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) cardNumber.getScene().getWindow();
        stage.close();
    }

    public interface PaymentCompleteListener {
        void onPaymentComplete(String paymentCardNumber);
    }
}
