package com.smartparcel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparcel.repository.UserRepositoryImpl;
import com.smartparcel.service.AuthServiceImpl;
import com.smartparcel.util.DatabaseUtil;
import com.smartparcel.util.JwtUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

import static com.smartparcel.util.DatabaseUtil.*;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    private Connection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Initialize all components
            connection = getConnection();
            UserRepositoryImpl userRepo = new UserRepositoryImpl(connection);
            JwtUtil jwtUtil = new JwtUtil();
            AuthServiceImpl authService = new AuthServiceImpl(userRepo, jwtUtil);
            ObjectMapper mapper = new ObjectMapper();

            // Store in ServletContext
            ServletContext context = sce.getServletContext();
            context.setAttribute("authService", authService);
            context.setAttribute("objectMapper", mapper);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Clean up resources
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            // Log error but don't throw as we're shutting down
            e.printStackTrace();
        }
    }
}
