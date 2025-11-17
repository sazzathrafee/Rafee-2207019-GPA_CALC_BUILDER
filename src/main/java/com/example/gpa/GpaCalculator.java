package com.example.gpa;

import com.example.gpa.model.Course;

import java.util.List;

public class GpaCalculator {

    public static double gradeToPoint(String grade) {
        if (grade == null) return 0.0;
        return switch (grade.trim()) {
            case "A+" -> 4.0;
            case "A" -> 3.75;
            case "A-" -> 3.5;
            case "B+" -> 3.25;
            case "B" -> 3.0;
            case "B-" -> 2.75;
            case "C+" -> 2.5;
            case "C" -> 2.25;
            case "C-" -> 2.0;
            case "D+" -> 1.75;
            case "D" -> 1.5;
            case "F" -> 0.0;
            default -> 0.0;
        };
    }

    public static double calculateGpa(List<Course> courses) {
        double totalQualityPoints = 0.0;
        double totalCredits = 0.0;
        for (Course c : courses) {
            double gradePoint = gradeToPoint(c.getGrade());
            totalQualityPoints += gradePoint * c.getCredit();
            totalCredits += c.getCredit();
        }
        if (totalCredits == 0) return 0.0;
        return totalQualityPoints / totalCredits;
    }
}
