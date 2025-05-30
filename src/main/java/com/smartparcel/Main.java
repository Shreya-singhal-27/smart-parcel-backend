package com.smartparcel;
import org.mindrot.jbcrypt.BCrypt;

public class Main {
    public static void main(String[] args) {
        String plainPassword = "test1234";

        // Generate salt and hash the password
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        System.out.println("Hashed password: " + hashedPassword);
    }
}


