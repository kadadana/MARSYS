package com.marsys.marsys.Repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabasePool {
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();

            String host = "ep-fragrant-term-a9jwf1gb-pooler.gwc.azure.neon.tech";
            String database = System.getenv("MARSYS_DB_DATABASE");
            String user = System.getenv("MARSYS_DB_USER");
            String password = System.getenv("MARSYS_DB_PASSWORD");

            if (database == null || user == null || password == null) {
                throw new IllegalStateException("Database environment variables are missing!");
            }

            String jdbcUrl = String.format(
                    "jdbc:postgresql://%s:5432/%s?user=%s&password=%s&sslmode=require",
                    host, database, user, password);
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Program Error");
            alert.setHeaderText("An error occured while getting database connection.");
            alert.setContentText(e.toString());
            alert.showAndWait();

            dataSource = null;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool couldn't started!");
        }
        return dataSource.getConnection();
    }
}
