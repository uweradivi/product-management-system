package com.store.auth;

import com.store.util.AppLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  AUTH MANAGER
 *  Handles user registration, login, and persistence.
 *  Uses SHA-256 password hashing (no plain-text passwords).
 *  Saves users to data/users.csv
 *
 *  Default admin account (created on first run):
 *    username: admin
 *    password: admin123
 *
 *  Roles:
 *    ADMIN    — created by system or seeded
 *    STAFF    — created by admin only
 *    CUSTOMER — self-registers on the login screen
 * ============================================================
 */
public class AuthManager {

    private static final String USERS_FILE = "data/users.csv";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<User> users = new ArrayList<>();
    private User currentUser = null;

    public AuthManager() {
        new File("data").mkdirs();
        loadUsers();
        seedDefaultAdmin();
    }

    // ── Authentication ────────────────────────────────────────

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        String hash = sha256(password);
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim())
                    && u.getPasswordHash().equals(hash)) {
                currentUser = u;
                AppLogger.success("LOGIN: " + u.getUsername() + " [" + u.getRole() + "]");
                return u;
            }
        }
        AppLogger.warn("LOGIN FAILED for username: " + username);
        return null;
    }

    public void logout() {
        if (currentUser != null) {
            AppLogger.info("LOGOUT: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn()  { return currentUser != null; }

    // ── Registration ──────────────────────────────────────────

    /**
     * Registers a new CUSTOMER — called from the public login screen.
     * Returns error message or null on success.
     */
    public String registerCustomer(String username, String password, String confirmPassword) {
        return register(username, password, confirmPassword, Role.CUSTOMER);
    }

    /**
     * Registers a new STAFF user — called by admin only.
     * Returns error message or null on success.
     */
    public String registerStaff(String username, String password, String confirmPassword) {
        return register(username, password, confirmPassword, Role.STAFF);
    }

    /**
     * Generic register — returns error message or null on success.
     */
    public String register(String username, String password, String confirmPassword, Role role) {
        if (username == null || username.trim().isEmpty())
            return "Username cannot be empty.";
        if (username.trim().length() < 3)
            return "Username must be at least 3 characters.";
        if (username.contains(","))
            return "Username cannot contain commas.";
        if (password == null || password.length() < 6)
            return "Password must be at least 6 characters.";
        if (!password.equals(confirmPassword))
            return "Passwords do not match.";
        if (findUser(username) != null)
            return "Username already exists.";

        String hash = sha256(password);
        String now  = LocalDateTime.now().format(FMT);
        User newUser = new User(username.trim(), hash, role, now);
        users.add(newUser);
        saveUsers();
        AppLogger.success("REGISTER: New user '" + username.trim() + "' role=" + role.name());
        return null;
    }

    // ── User Management (Admin only) ──────────────────────────

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public boolean deleteUser(String username) {
        User u = findUser(username);
        if (u == null) return false;
        users.remove(u);
        saveUsers();
        AppLogger.warn("USER DELETED: " + username);
        return true;
    }

    public boolean changeRole(String username, Role newRole) {
        User u = findUser(username);
        if (u == null) return false;
        u.setRole(newRole);
        saveUsers();
        AppLogger.info("ROLE CHANGED: " + username + " → " + newRole);
        return true;
    }

    public void linkCustomerRecord(String username, String customerId) {
        User u = findUser(username);
        if (u != null) {
            u.setLinkedCustomerId(customerId);
            saveUsers();
        }
    }

    public User findUser(String username) {
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(username.trim())) return u;
        return null;
    }

    // ── Persistence ───────────────────────────────────────────

    private void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            bw.write("# SmartStore Users — DO NOT EDIT MANUALLY");
            bw.newLine();
            for (User u : users) {
                bw.write(u.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            AppLogger.error("Failed to save users: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;
                try {
                    Role role = Role.valueOf(parts[2]);
                    String linkedCid = (parts.length >= 5 && !parts[4].isEmpty()) ? parts[4] : null;
                    users.add(new User(parts[0], parts[1], role, parts[3], linkedCid));
                } catch (Exception e) {
                    AppLogger.warn("Skipping malformed user line: " + line);
                }
            }
            AppLogger.info("Loaded " + users.size() + " user(s) from " + USERS_FILE);
        } catch (IOException e) {
            AppLogger.error("Failed to load users: " + e.getMessage());
        }
    }

    private void seedDefaultAdmin() {
        if (users.isEmpty()) {
            String hash = sha256("admin123");
            String now  = LocalDateTime.now().format(FMT);
            users.add(new User("admin", hash, Role.ADMIN, now));
            saveUsers();
            AppLogger.info("Default admin account created (username: admin, password: admin123)");
        }
    }

    // ── Password Hashing ──────────────────────────────────────

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
