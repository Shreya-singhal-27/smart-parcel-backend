package com.smartparcel.service;

import com.smartparcel.dto.AuthResponse;
import com.smartparcel.repository.UserRepositoryImpl;
import com.smartparcel.exception.AuthenticationException;
import com.smartparcel.model.User;
import com.smartparcel.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    private final UserRepositoryImpl userRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepositoryImpl userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(String email, String password) throws AuthenticationException {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new AuthenticationException("Invalid email or password");
            }

            if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                throw new AuthenticationException("Invalid email or password");
            }

            String token = jwtUtil.generateToken(user);
            return new AuthResponse(token, user.getRole(), "Login successful");
        } catch (SQLException e){
            throw new AuthenticationException("Database error during login");
        }
    }

    @Override
    public AuthResponse register(String email, String password, String role) throws AuthenticationException {
        try {
            if (userRepository.findByEmail(email) != null) {
                throw new AuthenticationException("Email already registered");
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(email, hashedPassword, role);
            userRepository.save(user);

            String token = jwtUtil.generateToken(user);
            return new AuthResponse(token, role, "Registration successful");
        } catch(SQLException e){
            throw new AuthenticationException("Database error during registration");
        }
    }
}

