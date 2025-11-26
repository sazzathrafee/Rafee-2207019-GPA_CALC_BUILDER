package com.example.gpa.controllers;

import com.example.gpa.GpaCalculator;
import com.example.gpa.model.Course;
import com.example.gpa.services.GpaSummaryService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
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
        double totalCredits = courses.stream().mapToDouble(Course::getCredit).sum();
        
        gpaLabel.setText(String.format("GPA: %.2f", gpa));
        
        // Save GPA summary with courses to database (background operation)
        GpaSummaryService.getInstance().saveSummaryWithCourses(gpa, totalCredits, courses);
    }
    
    @FXML
    private void handleGoHome(ActionEvent event) throws IOException {
        HomeController.loadHome(event);
    }
    
    @FXML
    private void handleViewMaster(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/gpa/master.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 650));
    }
}
