package com.example.gpa.database;

import com.example.gpa.model.GpaSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for GpaSummary table.
 * Handles all CRUD operations for GPA history.
 */
public class GpaSummaryDao {
    private final DatabaseManager dbManager;

    public GpaSummaryDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Insert a new GPA summary into the database
     * @return the ID of the inserted record, or -1 if failed
     */
    public int insertSummary(double gpa, double credits) {
        String insertSQL = "INSERT INTO gpa_summary (gpa, credits, timestamp) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            GpaSummary summary = new GpaSummary(gpa, credits);
            pstmt.setDouble(1, summary.getGpa());
            pstmt.setDouble(2, summary.getCredits());
            pstmt.setString(3, summary.getTimestamp());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        System.out.println("Inserted GPA summary with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting GPA summary: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Fetch all GPA summaries from the database
     * @return List of all GpaSummary objects, ordered by timestamp descending
     */
    public List<GpaSummary> fetchAllSummaries() {
        List<GpaSummary> summaries = new ArrayList<>();
        String selectSQL = "SELECT id, gpa, credits, timestamp FROM gpa_summary ORDER BY timestamp DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            while (rs.next()) {
                GpaSummary summary = new GpaSummary(
                    rs.getInt("id"),
                    rs.getDouble("gpa"),
                    rs.getDouble("credits"),
                    rs.getString("timestamp")
                );
                summaries.add(summary);
            }
            
            System.out.println("Fetched " + summaries.size() + " GPA summaries from database.");
            
        } catch (SQLException e) {
            System.err.println("Error fetching GPA summaries: " + e.getMessage());
            e.printStackTrace();
        }
        
        return summaries;
    }

    /**
     * Delete a GPA summary by ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteSummary(int id) {
        String deleteSQL = "DELETE FROM gpa_summary WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Deleted GPA summary with ID: " + id);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting GPA summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Update an existing GPA summary (optional feature)
     * @return true if updated successfully, false otherwise
     */
    public boolean updateSummary(GpaSummary summary) {
        String updateSQL = "UPDATE gpa_summary SET gpa = ?, credits = ?, timestamp = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setDouble(1, summary.getGpa());
            pstmt.setDouble(2, summary.getCredits());
            pstmt.setString(3, summary.getTimestamp());
            pstmt.setInt(4, summary.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Updated GPA summary with ID: " + summary.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating GPA summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get count of all summaries
     */
    public int getCount() {
        String countSQL = "SELECT COUNT(*) as count FROM gpa_summary";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSQL)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting count: " + e.getMessage());
        }
        
        return 0;
    }
}
