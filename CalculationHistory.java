package com.advancedScientificCalculator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
public class CalculationHistory {
    private static final int MAX_HISTORY = 50;
    public static void main(String[] args) {
        CalculationHistory history = new CalculationHistory();
        history.addEntry("sin(90)", 1.0);
    }
    public void addEntry(String operation, double result) {
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement deleteStatement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/scientific_calculator";
            String user = "root";
            String password = "Mysql@123";
            connection = DriverManager.getConnection(url, user, password);
            String insertSQL = "INSERT INTO calculation_history (operation, result, timestamp) VALUES (?, ?, ?)";
            insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setString(1, operation);
            insertStatement.setDouble(2, result);
            insertStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            insertStatement.executeUpdate();
            String deleteSQL = "DELETE FROM calculation_history WHERE id NOT IN (" +
                               "SELECT id FROM (" +
                               "  SELECT id FROM calculation_history " +
                               "  ORDER BY timestamp DESC " +
                               "  LIMIT ?) AS temp" +
                               ")";
            deleteStatement = connection.prepareStatement(deleteSQL);
            deleteStatement.setInt(1, MAX_HISTORY);
            int deletedRows = deleteStatement.executeUpdate();
            System.out.println("Number of rows deleted: " + deletedRows);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database operation failed.");
            e.printStackTrace();
        } finally {
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (deleteStatement != null) {
                try {
                    deleteStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
