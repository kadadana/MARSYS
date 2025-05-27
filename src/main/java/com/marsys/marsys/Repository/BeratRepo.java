package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.Employee;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BeratRepo {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured on database.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    public Employee getEmployeeModelById(String id) {
        Employee employee = null;
        String query = "SELECT * FROM \"EMPLOYEE\" WHERE \"ID\" = ?";
        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                employee = new Employee(
                        rs.getString("NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("POSITION"),
                        rs.getString("ID"),
                        rs.getString("PASSWORD"),
                        rs.getString("STORE_CODE"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("BIRTH_DATE"),
                        rs.getString("COUPON_CODE"));
                return employee;
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting employee model.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return employee;
    }

    public List<Employee> getAllEmployees() {
        String query = "SELECT * FROM \"EMPLOYEE\" ";

        List<Employee> employeeList = new ArrayList<>();
        Employee employee;

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employee = new Employee(rs.getString("NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("POSITION"),
                        rs.getString("ID"),
                        rs.getString("PASSWORD"),
                        rs.getString("STORE_CODE"),
                        rs.getString("START_DATE"),
                        rs.getString("END_DATE"),
                        rs.getString("BIRTH_DATE"),
                        rs.getString("COUPON_CODE"));
                if (employee.getEndDate() == null) {
                    employee.setEndDate("-");
                }

                employeeList.add(employee);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting employee list.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return employeeList;
    }

    public void deleteFromEmployeeById(String id) {
        String query = "DELETE FROM \"EMPLOYEE\" WHERE \"ID\" = ?";

        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured while deleting this employee.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    public void updateEmployeeTable(Employee employee) {
        String query = "UPDATE \"EMPLOYEE\" SET " +
                "\"ID\" = ?, " +
                "\"NAME\" = ?, " +
                "\"LAST_NAME\" = ?, " +
                "\"POSITION\" = ?, " +
                "\"PASSWORD\" = ?, " +
                "\"STORE_CODE\" = ?, " +
                "\"START_DATE\" = ?, " +
                "\"END_DATE\" = ?, " +
                "\"BIRTH_DATE\" = ?, " +
                "\"COUPON_CODE\" = ? " +
                "WHERE \"ID\" = ?";

        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getPosition());
            stmt.setString(5, employee.getPassword());
            stmt.setString(6, employee.getStoreCode());
            stmt.setString(7, employee.getStartDate());
            stmt.setString(8, employee.getEndDate());
            stmt.setString(9, employee.getBirthDate());
            stmt.setString(10, employee.getCouponCode());
            stmt.setString(11, employee.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while updating employee table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }

    public String getLatestEmployeeId() {
        String employeeId;
        String query = "SELECT \"ID\" FROM \"EMPLOYEE\" ORDER BY \"ID\" DESC LIMIT 1";
        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                employeeId = rs.getString("ID");
                return String.format("%03d", Integer.parseInt(employeeId) + 1);

            } else {
                return "001";
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while getting latest employee id.");
            alert.setContentText(e.toString());
            alert.showAndWait();
            return "001";
        }
    }

    public void insertIntoEmployeeTable(Employee employee) {

        String query = "INSERT INTO \"EMPLOYEE\" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabasePool.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getPosition());
            stmt.setString(5, employee.getPassword());
            stmt.setString(6, employee.getStoreCode());
            stmt.setString(7, employee.getStartDate());
            stmt.setString(8, employee.getEndDate());
            stmt.setString(9, employee.getBirthDate());
            stmt.setString(10, employee.getCouponCode());

            stmt.executeUpdate();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("An error occured while inserting into employee table.");
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
    }
}
