package com.marsys.marsys.Repository;

import com.marsys.marsys.Models.Campaign;
import com.marsys.marsys.Models.Employee;

import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BeratRepo {
    String url = "jdbc:postgresql://ep-fragrant-term-a9jwf1gb-pooler.gwc.azure.neon.tech:5432/MARSYS_DB?user=neondb_owner&password=npg_KkUHzrI37loY&sslmode=require";

    public Employee getEmployeeModelById(String id) {
        Employee employee = null;
        String query = "SELECT * FROM \"EMPLOYEE\" WHERE \"ID\" = ?";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
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
                        rs.getString("BIRTH_DATE"));
                return employee;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public List<Employee> getAllEmployees() {
        String query = "SELECT * FROM \"EMPLOYEE\" ";

        List<Employee> employeeList = new ArrayList<>();
        Employee employee;

        try (var conn = DriverManager.getConnection(url);
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
                        rs.getString("BIRTH_DATE"));
                if (employee.getEndDate() == null) {
                    employee.setEndDate("-");
                }

                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public void deleteFromEmployeeById(String id) {
        String query = "DELETE FROM \"EMPLOYEE\" WHERE \"ID\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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
                "\"END_DATE\" = ? " +
                "\"BIRTH_DATE\" = ? " +
                "WHERE \"ID\" = ?";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getPosition());
            stmt.setString(5, employee.getPassword());
            stmt.setString(6, employee.getStoreCode());
            stmt.setString(7, employee.getStartDate());
            stmt.setString(8, employee.getEndDate());
            stmt.setString(9, employee.getBirthDate());
            stmt.setString(10, employee.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLatestEmployeeId() {
        String employeeId;
        String query = "SELECT \"ID\" FROM \"EMPLOYEE\" ORDER BY \"ID\" DESC LIMIT 1";
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                employeeId = rs.getString("ID");
                return String.format("%03d", Integer.parseInt(employeeId) + 1);

            } else {
                return "001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "001";
        }
    }

    public void insertIntoEmployeeTable(Employee employee) {

        String query = "INSERT INTO \"EMPLOYEE\" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, employee.getFirstName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getPosition());
            stmt.setString(5, employee.getPassword());
            stmt.setString(6, employee.getStoreCode());
            stmt.setString(7, employee.getStartDate());
            stmt.setString(8, employee.getEndDate());
            stmt.setString(9, employee.getBirthDate());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
