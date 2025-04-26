package com.marsys.marsys.Controllers;

import com.marsys.marsys.Models.Session;
import javafx.event.ActionEvent;
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
        loadPage("/com/marsys/marsys/Views/login.fxml", mainContent);
    }

    private void loadPage(String fxmlPath, AnchorPane content) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node page = loader.load();

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

            if (page instanceof Region) {
                Region region = (Region) page;
                region.prefWidthProperty().bind(content.widthProperty());
                region.prefHeightProperty().bind(content.heightProperty());
                region.maxWidthProperty().bind(content.widthProperty());
                region.maxHeightProperty().bind(content.heightProperty());
            }

            content.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

}
