package com.marsys.marsys.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutController {

    @FXML
    private AnchorPane mainContent;

    public void initialize() {
        loadPage(mainContent);
    }

    private void loadPage(AnchorPane content) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marsys/marsys/Views/login.fxml"));
            Node page = loader.load();

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

            if (page instanceof Region region) {
                region.prefWidthProperty().bind(content.widthProperty());
                region.prefHeightProperty().bind(content.heightProperty());
                region.maxWidthProperty().bind(content.widthProperty());
                region.maxHeightProperty().bind(content.heightProperty());
            }

            content.getChildren().setAll(page);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    //Açmak istediğin fxml dosyasının yolunu ve onu açacak olan butonu vererek o fxml dosyasının açılmasını sağlayabilirsin
    public void loadPageByButton(String fxmlPath, Button btnOpen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) btnOpen.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured in this operation.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

}
