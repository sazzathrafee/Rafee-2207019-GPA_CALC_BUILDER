package com.example.gpa.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connection and initialization.
 * Creates database file and tables if they don't exist.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:gpa_history.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    /**
     * Singleton pattern to ensure single database connection
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Get active database connection
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    /**
     * Initialize database and create table if not exists
     */
    private void initializeDatabase() {
        String createGpaSummaryTable = """
            CREATE TABLE IF NOT EXISTS gpa_summary (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                gpa REAL NOT NULL,
                credits REAL NOT NULL,
                timestamp TEXT NOT NULL
            )
            """;
        
        String createCoursesTable = """
            CREATE TABLE IF NOT EXISTS courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                gpa_summary_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                code TEXT NOT NULL,
                credit REAL NOT NULL,
                teacher1 TEXT,
                teacher2 TEXT,
                grade TEXT NOT NULL,
                FOREIGN KEY (gpa_summary_id) REFERENCES gpa_summary(id) ON DELETE CASCADE
            )
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createGpaSummaryTable);
            stmt.execute(createCoursesTable);
            System.out.println("Database initialized successfully: gpa_history.db");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
