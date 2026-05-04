package com.store.component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  FILE I/O LAYER
 * ============================================================
 *  Responsible for persisting and loading:
 *    - Products  → data/products.csv
 *    - Customers → data/customers.csv
 *    - Sales log → data/sales_log.txt  (append-only)
 *
 *  CSV format chosen so files are human-readable in any editor.
 *
 *  Product CSV row:
 *    ELECTRONICS,<id>,<name>,<price>,<stock>,<warrantyYears>
 *    FOOD,<id>,<name>,<price>,<stock>,<expiryDate>
 *    CLOTHING,<id>,<name>,<price>,<stock>,<size>,<material>
 *
 *  Customer CSV row:
 *    <id>,<name>,<email>,<walletBalance>
 * ============================================================
 */
public class FileManager {

    private static final String DATA_DIR      = "data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.csv";
    private static final String CUSTOMERS_FILE= DATA_DIR + "customers.csv";
    private static final String SALES_LOG     = DATA_DIR + "sales_log.txt";

    public FileManager() {
        // Ensure the data directory exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ─────────────────────────────────────────────────────
    //  PRODUCTS – WRITE
    // ─────────────────────────────────────────────────────

    /**
     * Saves the full product list to products.csv (overwrites).
     */
    public void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            bw.write("# Product Management System — Products File");
            bw.newLine();
            bw.write("# Format: TYPE,id,name,price,stock,[extra fields]");
            bw.newLine();
            for (Product p : products) {
                bw.write(p.toCsv());
                bw.newLine();
            }
            System.out.println("  ✔ Products saved to " + PRODUCTS_FILE);
        } catch (IOException e) {
            System.out.println("  ✘ Error saving products: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    //  PRODUCTS – READ
    // ─────────────────────────────────────────────────────

    /**
     * Loads products from products.csv.  Returns an empty list if file absent.
     */
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                try {
                    String type = parts[0].toUpperCase();
                    switch (type) {
                        case "ELECTRONICS":
                            // ELECTRONICS,id,name,price,stock,warranty
                            products.add(new Electronics(
                                    parts[1], parts[2],
                                    Double.parseDouble(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    Integer.parseInt(parts[5])));
                            break;
                        case "FOOD":
                            // FOOD,id,name,price,stock,expiryDate
                            products.add(new Food(
                                    parts[1], parts[2],
                                    Double.parseDouble(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    parts[5]));
                            break;
                        case "CLOTHING":
                            // CLOTHING,id,name,price,stock,size,material
                            products.add(new Clothing(
                                    parts[1], parts[2],
                                    Double.parseDouble(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    parts[5], parts[6]));
                            break;
                        default:
                            System.out.println("  ⚠ Unknown product type on line " + lineNum + ": " + type);
                    }
                } catch (Exception e) {
                    System.out.println("  ⚠ Skipping malformed line " + lineNum + ": " + e.getMessage());
                }
            }
            System.out.println("  ✔ Loaded " + products.size() + " product(s) from " + PRODUCTS_FILE);
        } catch (IOException e) {
            System.out.println("  ✘ Error loading products: " + e.getMessage());
        }
        return products;
    }

    // ─────────────────────────────────────────────────────
    //  CUSTOMERS – WRITE
    // ─────────────────────────────────────────────────────

    public void saveCustomers(List<Customer> customers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            bw.write("# Product Management System — Customers File");
            bw.newLine();
            bw.write("# Format: id,name,email,walletBalance");
            bw.newLine();
            for (Customer c : customers) {
                bw.write(c.toCsv());
                bw.newLine();
            }
            System.out.println("  ✔ Customers saved to " + CUSTOMERS_FILE);
        } catch (IOException e) {
            System.out.println("  ✘ Error saving customers: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    //  CUSTOMERS – READ
    // ─────────────────────────────────────────────────────

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) return customers;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                try {
                    customers.add(new Customer(
                            parts[0], parts[1], parts[2],
                            Double.parseDouble(parts[3])));
                } catch (Exception e) {
                    System.out.println("  ⚠ Skipping malformed customer line " + lineNum + ": " + e.getMessage());
                }
            }
            System.out.println("  ✔ Loaded " + customers.size() + " customer(s) from " + CUSTOMERS_FILE);
        } catch (IOException e) {
            System.out.println("  ✘ Error loading customers: " + e.getMessage());
        }
        return customers;
    }

    // ─────────────────────────────────────────────────────
    //  SALES LOG – APPEND
    // ─────────────────────────────────────────────────────

    /**
     * Appends a single sales record to the log file.
     */
    public void logSale(String customerName, String productName, int qty, double total) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_LOG, true))) {
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write(timestamp + " | Customer: " + customerName
                    + " | Product: " + productName
                    + " | Qty: " + qty
                    + " | Total: $" + String.format("%.2f", total));
            bw.newLine();
        } catch (IOException e) {
            System.out.println("  ⚠ Could not write to sales log: " + e.getMessage());
        }
    }

    /**
     * Prints the entire sales log to the console.
     */
    public void printSalesLog() {
        File file = new File(SALES_LOG);
        if (!file.exists() || file.length() == 0) {
            System.out.println("  No sales recorded yet.");
            return;
        }
        System.out.println("\n  ── Sales Log ──────────────────────────────────");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }
        } catch (IOException e) {
            System.out.println("  ✘ Error reading sales log: " + e.getMessage());
        }
        System.out.println("  ────────────────────────────────────────────────");
    }

    /**
     * Returns the entire sales log as a String for JavaFX display.
     */
    public String getSalesLogText() {
        File file = new File(SALES_LOG);
        if (!file.exists() || file.length() == 0) return "No sales recorded yet.";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading sales log: " + e.getMessage();
        }
        return sb.toString();
    }
}
