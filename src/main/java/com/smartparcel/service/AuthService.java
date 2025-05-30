package com.smartparcel.service;

import com.smartparcel.dto.AuthResponse;
import com.smartparcel.exception.AuthenticationException;

public interface AuthService {
    AuthResponse login(String email, String password) throws AuthenticationException;
    AuthResponse register(String email, String password, String role) throws AuthenticationException;
}

