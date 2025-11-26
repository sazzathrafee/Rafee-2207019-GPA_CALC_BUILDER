package com.example.gpa.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    private Button startButton;

    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/gpa/entry.fxml"));
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 650));
    }
    
    @FXML
    private void handleLoadPrevious(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/gpa/master.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 650));
    }
    
    // Static utility method for other controllers to navigate home
    public static void loadHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HomeController.class.getResource("/com/example/gpa/home.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 900, 650));
    }
}
