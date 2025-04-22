package com.marsys.marsys.Repository;

import java.sql.*;

public class Repository {
    public String url = "jdbc:sqlite:src/main/resources/Repositories/MARSYS_DB.db";

    public String getCellById(String tableName, String columnName, String id) {
        String query = "SELECT [" + columnName + "] FROM [" + tableName + "] WHERE ID = ?";
        String cell = null;
        try (var conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cell = rs.getString(columnName);
            }
            return cell;
        } catch (SQLException e) {
            e.printStackTrace();  // Hata mesajını konsola yazdır
            return null;
        }
    }
}
