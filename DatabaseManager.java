package com.advancedScientificCalculator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
public class DatabaseManager {
    private static final int MAX_HISTORY = 50;
    public void addEntry(String operation, double result) {
        Connection connection = null;
        PreparedStatement countStatement = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement insertStatement = null;
        ResultSet countResult = null;
        try {
            String url = "jdbc:mysql://localhost:3306/scientific_calculator"; 
            String user = "root"; 
            String password = "Mysql@123"; 
            connection = DriverManager.getConnection(url, user, password);
            String countSQL = "SELECT COUNT(*) FROM calculation_history";
            countStatement = connection.prepareStatement(countSQL);
            countResult = countStatement.executeQuery();
            int rowCount = 0;
            if (countResult.next()) {
                rowCount = countResult.getInt(1);
            }
            if (rowCount >= MAX_HISTORY) {
                int recordsToDelete = rowCount - MAX_HISTORY + 1; 
                String deleteSQL = "DELETE FROM calculation_history ORDER BY timestamp ASC LIMIT ?";
                deleteStatement = connection.prepareStatement(deleteSQL);
                deleteStatement.setInt(1, recordsToDelete);
                deleteStatement.executeUpdate();
            }
            String insertSQL = "INSERT INTO calculation_history (operation, result, timestamp) VALUES (?, ?, ?)";
            insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setString(1, operation);
            insertStatement.setDouble(2, result);
            insertStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Database operation failed.");
            e.printStackTrace();
        } finally {
            try {
                if (countResult != null) countResult.close();
                if (countStatement != null) countStatement.close();
                if (deleteStatement != null) deleteStatement.close();
                if (insertStatement != null) insertStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Failed to close database resources.");
                e.printStackTrace();
            }
        }
    }
}
