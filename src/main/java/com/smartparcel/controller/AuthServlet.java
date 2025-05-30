package com.smartparcel.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparcel.dto.AuthResponse;
import com.smartparcel.dto.LoginRequest;
import com.smartparcel.dto.RegisterRequest;
import com.smartparcel.exception.AuthenticationException;
import com.smartparcel.service.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private AuthServiceImpl authService;
    private ObjectMapper objectMapper;

    public AuthServlet() {
    }


    public AuthServlet(AuthServiceImpl authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void init() throws ServletException {
        try {
            this.authService = (AuthServiceImpl) getServletContext().getAttribute("authService");
            this.objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");

            if (authService == null || objectMapper == null) {
                throw new ServletException("Required dependencies not initialized");
            }
        } catch (Exception e) {
            e.printStackTrace(); // or use logger
            throw new ServletException("Init failed", e);
        }
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("🚀 Reached AuthServlet doPost method");
        System.out.println("authService is null? " + (authService == null));
        System.out.println("objectMapper is null? " + (objectMapper == null));
        System.out.println("pathInfo: " + request.getPathInfo());
        String path = request.getPathInfo();

        if (path == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Path not found");
            return;
        }

        if (!isValidContentType(request)) {
            sendError(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
            return;
        }

        try {
            switch (path) {
                case "/login":
                    LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
                    handleLogin(loginRequest, response);
                    break;
                case "/register":
                    RegisterRequest registerRequest = objectMapper.readValue(request.getReader(), RegisterRequest.class);
                    handleRegistration(registerRequest, response);
                    break;
                default:
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Path not found");
            }
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            logger.error("JSON mapping failed", e);
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid data structure: " + e.getOriginalMessage());
        }catch (JsonParseException e) {
            logger.error("Invalid JSON format", e);
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    private void handleLogin(LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        try {
            logger.debug("Authenticating user: {}", loginRequest.getEmail());
            AuthResponse authResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            sendResponse(response, HttpServletResponse.SC_OK, authResponse);
        } catch (AuthenticationException e) {
            logger.warn("Login failed for user: {}", loginRequest.getEmail(), e);
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void handleRegistration(RegisterRequest registerRequest, HttpServletResponse response) throws IOException {
        try {
            if (!isValidEmail(registerRequest.getEmail()) || !isValidPassword(registerRequest.getPassword())) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid email or password format");
                return;
            }

            AuthResponse authResponse = authService.register(
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getRole()
            );
            sendResponse(response, HttpServletResponse.SC_CREATED, authResponse);
        } catch (AuthenticationException e) {
            logger.warn("Registration failed for email: {}", registerRequest.getEmail(), e);
            sendError(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    private void sendResponse(HttpServletResponse response, int statusCode, Object body) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        addSecurityHeaders(response);
        PrintWriter writer = response.getWriter();
        objectMapper.writeValue(writer, body);
        writer.flush();
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        addSecurityHeaders(response);
        PrintWriter writer = response.getWriter();
        writer.write("{\"error\":\"" + message + "\"}");
        writer.flush();
    }

    private boolean isValidContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("application/json");
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
    }
private boolean make(){
    return true;
}
}
