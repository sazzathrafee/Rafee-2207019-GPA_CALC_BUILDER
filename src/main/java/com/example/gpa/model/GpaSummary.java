package com.example.gpa.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a GPA calculation summary stored in the database.
 * This is separate from the Course model and only stores final GPA results.
 */
public class GpaSummary {
    private int id;
    private double gpa;
    private double credits;
    private String timestamp;
    private List<Course> courses; // List of courses associated with this GPA summary

    // Constructor for creating new summaries (without ID)
    public GpaSummary(double gpa, double credits) {
        this.gpa = gpa;
        this.credits = credits;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.courses = new ArrayList<>();
    }

    // Constructor for loading from database (with ID)
    public GpaSummary(int id, double gpa, double credits, String timestamp) {
        this.id = id;
        this.gpa = gpa;
        this.credits = credits;
        this.timestamp = timestamp;
        this.courses = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getGpa() {
        return gpa;
    }

    public double getCredits() {
        return credits;
    }

    public String getTimestamp() {
        return timestamp;
    }
    
    public List<Course> getCourses() {
        return courses;
    }

    // Setters (for updates)
    public void setId(int id) {
        this.id = id;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return String.format("GpaSummary{id=%d, gpa=%.2f, credits=%.1f, timestamp='%s', courses=%d}", 
                             id, gpa, credits, timestamp, courses.size());
    }
}
