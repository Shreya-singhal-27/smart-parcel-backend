package com.smartparcel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // or your DB name
    private static final String USER = "postgres";
    private static final String PASSWORD = "shrey#2795";

    static {
        try {
            Class.forName("org.postgresql.Driver"); // optional in modern Java
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

