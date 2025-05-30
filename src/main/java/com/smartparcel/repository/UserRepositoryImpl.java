package com.smartparcel.repository;

import com.smartparcel.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        }
        return null; // No user found
    }

    @Override
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, role) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());  // Already hashed
            stmt.setString(3, user.getRole());

            stmt.executeUpdate();
        }
    }
}

