package com.advancedScientificCalculator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.lang.Math;
public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scientific_calculator";
    private static final String USER = "root"; 
    private static final String PASS = "Mysql@123";
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Enter a mathematical operation (e.g., sin 45):");
                String operation = scanner.nextLine().toLowerCase(); 
                double result = evaluateOperation(operation);
                if (!Double.isNaN(result)) {
                    connection = DriverManager.getConnection(DB_URL, USER, PASS);
                    System.out.println("Connected to the database.");
                    String insertSQL = "INSERT INTO calculation_history (operation, result) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(insertSQL);
                    preparedStatement.setString(1, operation);
                    preparedStatement.setDouble(2, result);
                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("The calculation was inserted successfully!");
                        System.out.println("Result: " + result);
                    }
                }
                System.out.println("Do you want to perform another calculation? (yes/no)");
                String continueCalc = scanner.nextLine();
                if (!continueCalc.equalsIgnoreCase("yes")) {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private static double evaluateOperation(String operation) {
        try {
            String[] parts = operation.split("\\s+", 2);
            if (parts.length < 2) {
                System.err.println("Invalid format. Please use the format: <operation> <value>");
                return Double.NaN;
            }
            String command = parts[0].trim();
            double value = Double.parseDouble(parts[1].trim());
            
            switch (command) {
                case "sin":
                    return Math.sin(Math.toRadians(value));
                case "cos":
                    return Math.cos(Math.toRadians(value));
                case "tan":
                    return Math.tan(Math.toRadians(value));
                case "log":
                    if (value <= 0) {
                        System.out.println("Logarithm undefined for non-positive values.");
                        return Double.NaN;
                    }
                    return Math.log10(value);
                case "ln":
                    if (value <= 0) {
                        System.out.println("Natural logarithm undefined for non-positive values.");
                        return Double.NaN;
                    }
                    return Math.log(value);
                case "exp":
                    return Math.exp(value);
                default:
                    System.out.println("Unsupported operation: " + command);
                    return Double.NaN;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return Double.NaN;
        }
    }
}
