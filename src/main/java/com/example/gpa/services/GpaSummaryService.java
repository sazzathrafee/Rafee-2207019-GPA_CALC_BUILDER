package com.example.gpa.services;

import com.example.gpa.database.CourseDao;
import com.example.gpa.database.GpaSummaryDao;
import com.example.gpa.model.Course;
import com.example.gpa.model.GpaSummary;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service class for managing GPA summaries with background thread execution.
 * Uses ExecutorService and JavaFX Tasks to prevent blocking the UI thread.
 * Maintains an ObservableList that automatically updates the UI.
 */
public class GpaSummaryService {
    
    // Singleton instance
    private static GpaSummaryService instance;
    
    // Thread-safe executor for background operations
    private final ExecutorService executor;
    
    // DAO for database operations
    private final GpaSummaryDao dao;
    private final CourseDao courseDao;
    
    // Observable list that automatically updates UI (JavaFX thread-safe)
    public static final ObservableList<GpaSummary> gpaHistoryList = 
            FXCollections.observableArrayList();
    
    private GpaSummaryService() {
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true); // Daemon thread won't prevent app shutdown
            thread.setName("GPA-DB-Worker");
            return thread;
        });
        this.dao = new GpaSummaryDao();
        this.courseDao = new CourseDao();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized GpaSummaryService getInstance() {
        if (instance == null) {
            instance = new GpaSummaryService();
        }
        return instance;
    }
    
    /**
     * Save a new GPA summary in the background.
     * Updates the ObservableList on success.
     * 
     * @param gpa Calculated GPA value
     * @param credits Total credits used
     * @param onSuccess Callback executed on JavaFX thread when complete (optional)
     * @param onFailure Callback executed on JavaFX thread if error occurs (optional)
     */
    public void saveSummary(double gpa, double credits, 
                           Runnable onSuccess, 
                           Runnable onFailure) {
        
        Task<GpaSummary> saveTask = new Task<>() {
            @Override
            protected GpaSummary call() {
                System.out.println("[BG Thread] Saving GPA summary: " + gpa + ", Credits: " + credits);
                
                int id = dao.insertSummary(gpa, credits);
                
                if (id > 0) {
                    // Fetch the inserted record to get exact timestamp
                    List<GpaSummary> summaries = dao.fetchAllSummaries();
                    for (GpaSummary s : summaries) {
                        if (s.getId() == id) {
                            return s;
                        }
                    }
                }
                
                throw new RuntimeException("Failed to save GPA summary");
            }
        };
        
        saveTask.setOnSucceeded(event -> {
            GpaSummary savedSummary = saveTask.getValue();
            
            // Update ObservableList on JavaFX thread
            Platform.runLater(() -> {
                gpaHistoryList.add(0, savedSummary); // Add at beginning (newest first)
                System.out.println("[FX Thread] GPA summary added to list: " + savedSummary);
                
                if (onSuccess != null) {
                    onSuccess.run();
                }
            });
        });
        
        saveTask.setOnFailed(event -> {
            Throwable error = saveTask.getException();
            System.err.println("[ERROR] Failed to save GPA summary: " + error.getMessage());
            error.printStackTrace();
            
            if (onFailure != null) {
                Platform.runLater(onFailure);
            }
        });
        
        executor.submit(saveTask);
    }
    
    /**
     * Simplified save method without callbacks
     */
    public void saveSummary(double gpa, double credits) {
        saveSummary(gpa, credits, null, null);
    }
    
    /**
     * Load all GPA summaries from database in the background.
     * Populates the ObservableList.
     * Call this on application startup.
     * 
     * @param onComplete Callback when loading finishes (optional)
     */
    public void loadAllSummaries(Runnable onComplete) {
        
        Task<List<GpaSummary>> loadTask = new Task<>() {
            @Override
            protected List<GpaSummary> call() {
                System.out.println("[BG Thread] Loading all GPA summaries from database...");
                return dao.fetchAllSummaries();
            }
        };
        
        loadTask.setOnSucceeded(event -> {
            List<GpaSummary> summaries = loadTask.getValue();
            
            // Update ObservableList on JavaFX thread
            Platform.runLater(() -> {
                gpaHistoryList.clear();
                gpaHistoryList.addAll(summaries);
                System.out.println("[FX Thread] Loaded " + summaries.size() + " summaries into ObservableList");
                
                if (onComplete != null) {
                    onComplete.run();
                }
            });
        });
        
        loadTask.setOnFailed(event -> {
            Throwable error = loadTask.getException();
            System.err.println("[ERROR] Failed to load summaries: " + error.getMessage());
            error.printStackTrace();
        });
        
        executor.submit(loadTask);
    }
    
    /**
     * Simplified load method without callback
     */
    public void loadAllSummaries() {
        loadAllSummaries(null);
    }
    
    /**
     * Delete a GPA summary by ID in the background.
     * Removes from ObservableList on success.
     * 
     * @param id The ID of the summary to delete
     * @param onSuccess Callback on success (optional)
     * @param onFailure Callback on failure (optional)
     */
    public void deleteSummary(int id, Runnable onSuccess, Runnable onFailure) {
        
        Task<Boolean> deleteTask = new Task<>() {
            @Override
            protected Boolean call() {
                System.out.println("[BG Thread] Deleting GPA summary with ID: " + id);
                return dao.deleteSummary(id);
            }
        };
        
        deleteTask.setOnSucceeded(event -> {
            boolean success = deleteTask.getValue();
            
            if (success) {
                Platform.runLater(() -> {
                    // Remove from ObservableList
                    gpaHistoryList.removeIf(summary -> summary.getId() == id);
                    System.out.println("[FX Thread] Removed summary ID " + id + " from list");
                    
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                });
            } else {
                if (onFailure != null) {
                    Platform.runLater(onFailure);
                }
            }
        });
        
        deleteTask.setOnFailed(event -> {
            Throwable error = deleteTask.getException();
            System.err.println("[ERROR] Failed to delete summary: " + error.getMessage());
            
            if (onFailure != null) {
                Platform.runLater(onFailure);
            }
        });
        
        executor.submit(deleteTask);
    }
    
    /**
     * Simplified delete method without callbacks
     */
    public void deleteSummary(int id) {
        deleteSummary(id, null, null);
    }
    
    /**
     * Update an existing GPA summary (optional feature)
     * 
     * @param summary The updated summary object
     * @param onSuccess Callback on success
     * @param onFailure Callback on failure
     */
    public void updateSummary(GpaSummary summary, Runnable onSuccess, Runnable onFailure) {
        
        Task<Boolean> updateTask = new Task<>() {
            @Override
            protected Boolean call() {
                System.out.println("[BG Thread] Updating GPA summary ID: " + summary.getId());
                return dao.updateSummary(summary);
            }
        };
        
        updateTask.setOnSucceeded(event -> {
            boolean success = updateTask.getValue();
            
            if (success) {
                Platform.runLater(() -> {
                    // Update in ObservableList
                    for (int i = 0; i < gpaHistoryList.size(); i++) {
                        if (gpaHistoryList.get(i).getId() == summary.getId()) {
                            gpaHistoryList.set(i, summary);
                            break;
                        }
                    }
                    System.out.println("[FX Thread] Updated summary in list");
                    
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                });
            } else {
                if (onFailure != null) {
                    Platform.runLater(onFailure);
                }
            }
        });
        
        updateTask.setOnFailed(event -> {
            Throwable error = updateTask.getException();
            System.err.println("[ERROR] Failed to update summary: " + error.getMessage());
            
            if (onFailure != null) {
                Platform.runLater(onFailure);
            }
        });
        
        executor.submit(updateTask);
    }
    
    /**
     * Get the current ObservableList (for binding to UI components)
     */
    public ObservableList<GpaSummary> getGpaHistoryList() {
        return gpaHistoryList;
    }
    
    /**
     * Save a GPA summary with associated courses.
     * 
     * @param gpa Calculated GPA value
     * @param credits Total credits
     * @param courses List of courses
     * @param onSuccess Callback on success (optional)
     * @param onFailure Callback on failure (optional)
     */
    public void saveSummaryWithCourses(double gpa, double credits, List<Course> courses,
                                      Runnable onSuccess, Runnable onFailure) {
        
        Task<GpaSummary> saveTask = new Task<>() {
            @Override
            protected GpaSummary call() {
                System.out.println("[BG Thread] Saving GPA summary with " + courses.size() + " courses");
                
                // Save GPA summary first
                int id = dao.insertSummary(gpa, credits);
                
                if (id > 0) {
                    // Save all courses
                    courseDao.insertCourses(id, courses);
                    
                    // Fetch the inserted record with timestamp
                    List<GpaSummary> summaries = dao.fetchAllSummaries();
                    for (GpaSummary s : summaries) {
                        if (s.getId() == id) {
                            s.setCourses(courses);
                            return s;
                        }
                    }
                }
                
                throw new RuntimeException("Failed to save GPA summary with courses");
            }
        };
        
        saveTask.setOnSucceeded(event -> {
            GpaSummary savedSummary = saveTask.getValue();
            
            Platform.runLater(() -> {
                gpaHistoryList.add(0, savedSummary);
                System.out.println("[FX Thread] GPA summary with courses added to list: " + savedSummary);
                
                if (onSuccess != null) {
                    onSuccess.run();
                }
            });
        });
        
        saveTask.setOnFailed(event -> {
            Throwable error = saveTask.getException();
            System.err.println("[ERROR] Failed to save summary with courses: " + error.getMessage());
            
            if (onFailure != null) {
                Platform.runLater(onFailure);
            }
        });
        
        executor.submit(saveTask);
    }
    
    /**
     * Simplified save method with courses
     */
    public void saveSummaryWithCourses(double gpa, double credits, List<Course> courses) {
        saveSummaryWithCourses(gpa, credits, courses, null, null);
    }
    
    /**
     * Load courses for a specific GPA summary.
     * 
     * @param summaryId The GPA summary ID
     * @param onSuccess Callback with loaded courses (executed on JavaFX thread)
     * @param onFailure Callback on failure
     */
    public void loadCoursesForSummary(int summaryId, 
                                     java.util.function.Consumer<List<Course>> onSuccess,
                                     Runnable onFailure) {
        
        Task<List<Course>> loadTask = new Task<>() {
            @Override
            protected List<Course> call() {
                System.out.println("[BG Thread] Loading courses for summary ID: " + summaryId);
                return courseDao.fetchCoursesByGpaSummaryId(summaryId);
            }
        };
        
        loadTask.setOnSucceeded(event -> {
            List<Course> courses = loadTask.getValue();
            
            Platform.runLater(() -> {
                System.out.println("[FX Thread] Loaded " + courses.size() + " courses for summary " + summaryId);
                
                // Update the summary in the list with courses
                for (GpaSummary summary : gpaHistoryList) {
                    if (summary.getId() == summaryId) {
                        summary.setCourses(courses);
                        break;
                    }
                }
                
                if (onSuccess != null) {
                    onSuccess.accept(courses);
                }
            });
        });
        
        loadTask.setOnFailed(event -> {
            Throwable error = loadTask.getException();
            System.err.println("[ERROR] Failed to load courses: " + error.getMessage());
            
            if (onFailure != null) {
                Platform.runLater(onFailure);
            }
        });
        
        executor.submit(loadTask);
    }
    
    /**
     * Shutdown the executor service gracefully.
     * Call this when application is closing.
     */
    public void shutdown() {
        System.out.println("Shutting down GpaSummaryService executor...");
        executor.shutdown();
    }
}
