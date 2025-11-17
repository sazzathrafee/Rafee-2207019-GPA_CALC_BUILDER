package com.example.gpa.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
}
