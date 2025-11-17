package com.example.gpa.controllers;

import com.example.gpa.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EntryController {
    @FXML private TextField targetCreditsField;
    @FXML private TextField nameField;
    @FXML private TextField codeField;
    @FXML private TextField creditField;
    @FXML private TextField teacher1Field;
    @FXML private TextField teacher2Field;
    @FXML private ComboBox<String> gradeCombo;
    @FXML private Button addButton;
    @FXML private Button calcButton;
    @FXML private TableView<Course> table;
    @FXML private TableColumn<Course, String> colName;
    @FXML private TableColumn<Course, String> colCode;
    @FXML private TableColumn<Course, Double> colCredit;
    @FXML private TableColumn<Course, String> colGrade;
    @FXML private TableColumn<Course, Void> colAction;
    @FXML private Label statusLabel;

    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        gradeCombo.setItems(FXCollections.observableArrayList("A+","A","A-","B+","B","B-","C+","C","C-","D+","D","F"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        
        // Add edit and delete buttons to each row
        colAction.setCellFactory(param -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button editBtn = new javafx.scene.control.Button("Edit");
            private final javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("Delete");
            private final javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5, editBtn, deleteBtn);
            {
                editBtn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    populateFormForEdit(course);
                    courses.remove(course);
                    statusLabel.setText("Editing: " + course.getName());
                    updateCalcButton();
                });
                deleteBtn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    courses.remove(course);
                    statusLabel.setText("Course removed: " + course.getName());
                    updateCalcButton();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        table.setItems(courses);
        calcButton.setDisable(true);
        
        // Disable form fields until target is set
        disableFormFields(true);
        
        // Listen to target credits changes
        targetCreditsField.textProperty().addListener((obs, oldVal, newVal) -> onTargetChanged());
    }
    
    private void onTargetChanged() {
        String targetText = targetCreditsField.getText().trim();
        if (targetText.isEmpty()) {
            disableFormFields(true);
            statusLabel.setText("Please enter target credits first");
            return;
        }
        
        try {
            double target = Double.parseDouble(targetText);
            if (target <= 0) {
                disableFormFields(true);
                statusLabel.setText("Target credits must be positive");
                return;
            }
            disableFormFields(false);
            statusLabel.setText("Target credits set. You can now add courses.");
            updateCalcButton();
        } catch (NumberFormatException e) {
            disableFormFields(true);
            statusLabel.setText("Invalid target credits format");
        }
    }
    
    private void disableFormFields(boolean disable) {
        nameField.setDisable(disable);
        codeField.setDisable(disable);
        creditField.setDisable(disable);
        teacher1Field.setDisable(disable);
        teacher2Field.setDisable(disable);
        gradeCombo.setDisable(disable);
        addButton.setDisable(disable);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String creditText = creditField.getText().trim();
        String t1 = teacher1Field.getText().trim();
        String t2 = teacher2Field.getText().trim();
        String grade = gradeCombo.getValue();

        if (name.isEmpty() || code.isEmpty() || creditText.isEmpty() || grade == null) {
            showAlert("Validation", "Please fill course name, code, credit and select grade.");
            return;
        }
        
        // Validate course code format
        if (code.contains(" ")) {
            showAlert("Validation Error", "Course code cannot contain spaces. Example: CSE2200 (not CSE 2200)");
            return;
        }
        if (!code.matches(".*\\d{4}$")) {
            showAlert("Validation Error", "Course code must end with exactly 4 digits. Example: CSE2200");
            return;
        }
        
        // Validate credit input
        double credit;
        try {
            // Check for multiple decimal points
            long decimalCount = creditText.chars().filter(ch -> ch == '.').count();
            if (decimalCount > 1) {
                showAlert("Validation Error", "Credit cannot have multiple decimal points.");
                return;
            }
            credit = Double.parseDouble(creditText);
            if (credit <= 0) {
                showAlert("Validation Error", "Credit must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid credit format. Please enter a valid number (e.g., 3 or 3.5).");
            return;
        }

        Course c = new Course(name, code, credit, t1, t2, grade);
        courses.add(c);
        clearForm();
        statusLabel.setText("Course added successfully!");
        updateCalcButton();
    }

    private void updateCalcButton() {
        double target = parseTarget();
        if (target <= 0) { 
            calcButton.setDisable(true); 
            return; 
        }
        double sum = courses.stream().mapToDouble(Course::getCredit).sum();
        
        // Enable calculate button if sum > 0 and sum <= target
        boolean canCalculate = sum > 0 && sum <= target;
        calcButton.setDisable(!canCalculate);
        
        if (sum > target) {
            statusLabel.setText(String.format("Credits exceeded! %.1f / %.1f (Remove courses)", sum, target));
        } else if (sum > 0) {
            statusLabel.setText(String.format("Credits: %.1f / %.1f (Can calculate GPA)", sum, target));
        } else {
            statusLabel.setText(String.format("Credits: %.1f / %.1f (Add courses)", sum, target));
        }
    }

    private double parseTarget() {
        try { return Double.parseDouble(targetCreditsField.getText().trim()); }
        catch (Exception e) { return -1; }
    }

    @FXML
    private void handleCalculate(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gpa/result.fxml"));
        Parent root = loader.load();
        ResultController rc = loader.getController();
        rc.setData(List.copyOf(courses));
        Stage stage = (Stage) calcButton.getScene().getWindow();
        stage.setScene(new Scene(root, 900, 650));
    }

    private void populateFormForEdit(Course course) {
        nameField.setText(course.getName());
        codeField.setText(course.getCode());
        creditField.setText(String.valueOf(course.getCredit()));
        teacher1Field.setText(course.getTeacher1());
        teacher2Field.setText(course.getTeacher2());
        gradeCombo.setValue(course.getGrade());
    }

    private void clearForm() {
        nameField.clear(); codeField.clear(); creditField.clear(); teacher1Field.clear(); teacher2Field.clear(); gradeCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
}
