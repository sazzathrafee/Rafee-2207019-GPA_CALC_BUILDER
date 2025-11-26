package com.example.gpa.controllers;

import com.example.gpa.model.GpaSummary;
import com.example.gpa.services.GpaSummaryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MasterController {
    @FXML private TableView<GpaSummary> masterTable;
    @FXML private TableColumn<GpaSummary, Integer> colId;
    @FXML private TableColumn<GpaSummary, Double> colGpa;
    @FXML private TableColumn<GpaSummary, Double> colCredits;
    @FXML private TableColumn<GpaSummary, String> colTimestamp;
    @FXML private TableColumn<GpaSummary, Void> colAction;
    @FXML private Label statusLabel;
    
    @FXML
    public void initialize() {
        // Bind table columns to GpaSummary properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        
        // Format GPA column to 2 decimal places
        colGpa.setCellFactory(column -> new TableCell<GpaSummary, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });
        
        // Add action buttons (Edit and Delete) to each row
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            {
                editBtn.setOnAction(event -> {
                    GpaSummary summary = getTableView().getItems().get(getIndex());
                    handleEditSummary(summary);
                });
                deleteBtn.setOnAction(event -> {
                    GpaSummary summary = getTableView().getItems().get(getIndex());
                    handleDeleteSummary(summary);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        // Bind table to ObservableList from GpaSummaryService
        masterTable.setItems(GpaSummaryService.gpaHistoryList);
        
        statusLabel.setText("Loaded " + GpaSummaryService.gpaHistoryList.size() + " saved GPA records.");
    }
    
    private void handleEditSummary(GpaSummary summary) {
        // Load courses for this summary first
        GpaSummaryService.getInstance().loadCoursesForSummary(
            summary.getId(),
            courses -> {
                // Courses loaded successfully, navigate to entry page
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gpa/entry.fxml"));
                    Parent root = loader.load();
                    EntryController controller = loader.getController();
                    
                    // Load the previous session data into the entry controller
                    controller.loadPreviousSession(summary, courses);
                    
                    Stage stage = (Stage) masterTable.getScene().getWindow();
                    stage.setScene(new Scene(root, 1000, 650));
                    
                } catch (IOException e) {
                    statusLabel.setText("Error loading entry page: " + e.getMessage());
                }
            },
            () -> statusLabel.setText("Error loading courses for this session")
        );
    }
    
    private void handleDeleteSummary(GpaSummary summary) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete GPA Record");
        confirm.setContentText("Are you sure you want to delete this GPA record?\nGPA: " + 
                String.format("%.2f", summary.getGpa()) + " | Credits: " + summary.getCredits());
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                GpaSummaryService.getInstance().deleteSummary(
                    summary.getId(),
                    () -> statusLabel.setText("Deleted GPA record #" + summary.getId()),
                    () -> statusLabel.setText("Error deleting record")
                );
            }
        });
    }
    
    @FXML
    private void handleGoHome(ActionEvent event) throws IOException {
        HomeController.loadHome(event);
    }
}
