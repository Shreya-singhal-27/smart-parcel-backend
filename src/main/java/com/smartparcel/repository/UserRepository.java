package com.smartparcel.repository;

import com.smartparcel.model.User;
import java.sql.SQLException;

public interface UserRepository {
    User findByEmail(String email) throws SQLException;
    void save(User user) throws SQLException;
}


