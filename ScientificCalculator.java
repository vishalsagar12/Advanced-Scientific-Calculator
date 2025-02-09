package com.advancedScientificCalculator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.lang.Math;
public class ScientificCalculator {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scientific_calculator";
    private static final String USER = "root";
    private static final String PASS = "Mysql@123";
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Enter a mathematical operation");
                String operation = scanner.nextLine();
                double result = evaluateOperation(operation);

                if (Double.isNaN(result)) {
                    System.out.println("Invalid operation. Please try again.");
                    continue;
                }
                try {
                    connection = DriverManager.getConnection(DB_URL, USER, PASS);
                    String insertSQL = "INSERT INTO calculation_history (operation, result) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(insertSQL);
                    preparedStatement.setString(1, operation);
                    preparedStatement.setDouble(2, result);

                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("The calculation was inserted successfully!");
                        System.out.println("Result: " + result);
                    }
                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage());
                } finally {
                    try {
                        if (preparedStatement != null) preparedStatement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resources: " + e.getMessage());
                    }
                }

                System.out.println("Do you want to perform another calculation? (yes/no)");
                String continueCalc = scanner.nextLine();
                if (!continueCalc.equalsIgnoreCase("yes")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static double evaluateOperation(String operation) {
        try {
            operation = operation.trim();
            if (operation.matches("\\d+\\s*[+\\-*/]\\s*\\d+")) {
                return evaluateBasicOperation(operation);
            } else if (operation.matches("(sin|cos|tan|log|ln|exp)\\(\\d+\\.?\\d*\\)")) {
                return evaluateSpecialFunction(operation);
            } else {
                System.out.println("Unsupported operation or invalid format.");
                return Double.NaN;
            }
        } catch (Exception e) {
            System.out.println("Error in evaluating the operation: " + e.getMessage());
            return Double.NaN;
        }
    }

    private static double evaluateBasicOperation(String operation) {
        try {
            if (operation.contains("+")) {
                String[] tokens = operation.split("\\+");
                return Double.parseDouble(tokens[0].trim()) + Double.parseDouble(tokens[1].trim());
            } else if (operation.contains("-")) {
                String[] tokens = operation.split("-");
                return Double.parseDouble(tokens[0].trim()) - Double.parseDouble(tokens[1].trim());
            } else if (operation.contains("*")) {
                String[] tokens = operation.split("\\*");
                return Double.parseDouble(tokens[0].trim()) * Double.parseDouble(tokens[1].trim());
            } else if (operation.contains("/")) {
                String[] tokens = operation.split("/");
                return Double.parseDouble(tokens[0].trim()) / Double.parseDouble(tokens[1].trim());
            }
            return Double.NaN;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + e.getMessage());
            return Double.NaN;
        }
    }

    private static double evaluateSpecialFunction(String operation) {
        try {
            if (operation.startsWith("sin")) {
                String value = operation.replace("sin(", "").replace(")", "").trim();
                return Math.sin(Math.toRadians(Double.parseDouble(value)));
            } else if (operation.startsWith("cos")) {
                String value = operation.replace("cos(", "").replace(")", "").trim();
                return Math.cos(Math.toRadians(Double.parseDouble(value)));
            } else if (operation.startsWith("tan")) {
                String value = operation.replace("tan(", "").replace(")", "").trim();
                return Math.tan(Math.toRadians(Double.parseDouble(value)));
            } else if (operation.startsWith("log")) {
                String value = operation.replace("log(", "").replace(")", "").trim();
                return Math.log10(Double.parseDouble(value));
            } else if (operation.startsWith("ln")) {
                String value = operation.replace("ln(", "").replace(")", "").trim();
                return Math.log(Double.parseDouble(value));
            } else if (operation.startsWith("exp")) {
                String value = operation.replace("exp(", "").replace(")", "").trim();
                return Math.exp(Double.parseDouble(value));
            }
            return Double.NaN;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + e.getMessage());
            return Double.NaN;
        }
    }
}