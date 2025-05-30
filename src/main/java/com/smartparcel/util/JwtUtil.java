package com.smartparcel.util;

import com.smartparcel.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private final String secretKey = "your-secret-key"; // Use a secure key in production
    private final long expirationMs = 86400000; // 1 day

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
}

