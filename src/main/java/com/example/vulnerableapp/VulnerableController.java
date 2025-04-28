
package com.example.vulnerableapp;

import java.io.*;
import java.sql.*;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class VulnerableController {

    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "password123";

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + hashPassword(password) + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return "Login successful";
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Invalid credentials";
    }

    @GetMapping("/exec")
    public String executeCommand(@RequestParam String cmd) {
        StringBuilder output = new StringBuilder();
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    @PostMapping("/admin")
    public String adminAccess(@RequestBody String secret, HttpServletRequest request) {
        if (secret.equals(ADMIN_PASS)) {
            return "Welcome, Admin!";
        } else {
            return "Access denied";
        }
    }
}
