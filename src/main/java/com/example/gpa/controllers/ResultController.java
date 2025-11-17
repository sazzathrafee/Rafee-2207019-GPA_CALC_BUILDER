package com.example.gpa.controllers;

import com.example.gpa.GpaCalculator;
import com.example.gpa.model.Course;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ResultController {
    @FXML private TableView<Course> resultTable;
    @FXML private TableColumn<Course, String> rName;
    @FXML private TableColumn<Course, String> rCode;
    @FXML private TableColumn<Course, Double> rCredit;
    @FXML private TableColumn<Course, String> rGrade;
    @FXML private Label gpaLabel;

    public void setData(List<Course> courses) {
        rName.setCellValueFactory(new PropertyValueFactory<>("name"));
        rCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        rCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        rGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        resultTable.setItems(FXCollections.observableArrayList(courses));
        double gpa = GpaCalculator.calculateGpa(courses);
        gpaLabel.setText(String.format("GPA: %.2f", gpa));
    }
}
