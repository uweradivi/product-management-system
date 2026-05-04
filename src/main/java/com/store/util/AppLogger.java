package com.store.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  APPLICATION LOGGER  (Bonus Feature)
 * ============================================================
 *  Centralized, singleton logger that writes timestamped
 *  entries to  data/app.log  with level tagging.
 *
 *  Levels:  INFO | WARN | ERROR | SUCCESS
 *
 *  Usage:
 *    AppLogger.info("Product P001 added");
 *    AppLogger.error("Invalid price: " + ex.getMessage());
 * ============================================================
 */
public class AppLogger {

    private static final String LOG_FILE = "data/app.log";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Singleton — no instantiation needed
    private AppLogger() {}

    /** Ensure the data directory and log file exist */
    private static void ensureFile() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
    }

    /** Core write method — appends a line to app.log */
    private static void write(String level, String message) {
        ensureFile();
        String entry = "[" + LocalDateTime.now().format(FMT) + "] "
                + "[" + level + "] " + message;
        // Also print to console for dev visibility
        System.out.println(entry);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Logger write failed: " + e.getMessage());
        }
    }

    public static void info(String message)    { write("INFO   ", message); }
    public static void warn(String message)    { write("WARN   ", message); }
    public static void error(String message)   { write("ERROR  ", message); }
    public static void success(String message) { write("SUCCESS", message); }

    /**
     * Reads the entire application log as a String for UI display.
     */
    public static String readLog() {
        File file = new File(LOG_FILE);
        if (!file.exists()) return "No application log entries yet.";
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading log: " + e.getMessage();
        }
        return sb.toString();
    }

    /**
     * Clears the log file.
     */
    public static void clearLog() {
        File file = new File(LOG_FILE);
        if (file.exists()) file.delete();
        info("Log cleared by user.");
    }
}
