package com.example.gpa.database;

import com.example.gpa.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entities.
 * Handles CRUD operations for courses linked to GPA summaries.
 */
public class CourseDao {
    
    /**
     * Insert multiple courses for a GPA summary
     */
    public void insertCourses(int gpaSummaryId, List<Course> courses) {
        String sql = "INSERT INTO courses (gpa_summary_id, name, code, credit, teacher1, teacher2, grade) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Course course : courses) {
                pstmt.setInt(1, gpaSummaryId);
                pstmt.setString(2, course.getName());
                pstmt.setString(3, course.getCode());
                pstmt.setDouble(4, course.getCredit());
                pstmt.setString(5, course.getTeacher1());
                pstmt.setString(6, course.getTeacher2());
                pstmt.setString(7, course.getGrade());
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            System.out.println("Inserted " + courses.size() + " courses for GPA summary ID: " + gpaSummaryId);
            
        } catch (SQLException e) {
            System.err.println("Error inserting courses: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Fetch all courses for a specific GPA summary
     */
    public List<Course> fetchCoursesByGpaSummaryId(int gpaSummaryId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT name, code, credit, teacher1, teacher2, grade " +
                     "FROM courses WHERE gpa_summary_id = ? ORDER BY id";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, gpaSummaryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getString("name"),
                    rs.getString("code"),
                    rs.getDouble("credit"),
                    rs.getString("teacher1"),
                    rs.getString("teacher2"),
                    rs.getString("grade")
                );
                courses.add(course);
            }
            
            System.out.println("Fetched " + courses.size() + " courses for GPA summary ID: " + gpaSummaryId);
            
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return courses;
    }
    
    /**
     * Delete all courses for a specific GPA summary
     * Note: CASCADE delete should handle this automatically, 
     * but this method is provided for explicit deletion if needed
     */
    public boolean deleteCoursesForSummary(int gpaSummaryId) {
        String sql = "DELETE FROM courses WHERE gpa_summary_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, gpaSummaryId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " courses for GPA summary ID: " + gpaSummaryId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting courses: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
