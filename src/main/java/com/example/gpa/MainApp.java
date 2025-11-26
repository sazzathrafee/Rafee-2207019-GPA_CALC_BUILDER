package com.example.gpa;

import com.example.gpa.services.GpaSummaryService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load GPA history from database in background
        GpaSummaryService.getInstance().loadAllSummaries(() -> {
            System.out.println("GPA history loaded successfully on startup");
        });
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gpa/home.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);
        
        // Load CSS if available
        var cssUrl = getClass().getResource("/com/example/gpa/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        
        primaryStage.setTitle("GPA Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        // Gracefully shutdown service on app close
        GpaSummaryService.getInstance().shutdown();
        System.out.println("Application stopped gracefully");
    }
}
