package com.marsys.marsys;

import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MARSYS_Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/marsys/marsys/Views/layout.fxml")));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setTitle("MARSYS");
        primaryStage.show();

        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);
    }

    public static void main(String[] args) {
        launch();
    }
}
