package com.store.component;

import java.io.Serializable;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private String email;
    private double walletBalance;

    public Customer(String customerId, String name, String email, double walletBalance) {
        this.customerId    = customerId;
        this.name          = name;
        this.email         = email;
        this.walletBalance = walletBalance;
    }

    /**
     * Attempts to purchase a product. Throws exceptions on failure.
     */
    public void purchase(Product product, int quantity) {
        double totalCost = (product.getPrice() - product.applyDiscount()) * quantity;

        System.out.println("\n  [PURCHASE] " + name + " → " + quantity + " × " + product.getName());
        System.out.printf("  Total cost (after discount): $%.2f%n", totalCost);
        System.out.printf("  Your wallet balance        : $%.2f%n", walletBalance);

        if (walletBalance < totalCost) {
            throw new InsufficientBalanceException(name, totalCost, walletBalance);
        }

        product.sell(quantity);          // may throw OutOfStockException
        walletBalance -= totalCost;
        System.out.printf("  ✔ Purchase SUCCESSFUL! Remaining balance: $%.2f%n", walletBalance);
    }

    public void topUpWallet(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Top-up amount must be positive.");
        walletBalance += amount;
        System.out.printf("  ✔ Wallet topped up by $%.2f → New balance: $%.2f%n", amount, walletBalance);
    }

    public void showProfile() {
        System.out.println("  ┌─────────────────────────────────────");
        System.out.println("  │ Customer ID : " + customerId);
        System.out.println("  │ Name        : " + name);
        System.out.println("  │ Email       : " + email);
        System.out.printf ("  │ Wallet      : $%.2f%n", walletBalance);
        System.out.println("  └─────────────────────────────────────");
    }

    /** CSV format: id,name,email,walletBalance */
    public String toCsv() {
        return customerId + "," + name + "," + email + "," + walletBalance;
    }

    // Getters
    public String getCustomerId()    { return customerId; }
    public String getName()          { return name; }
    public String getEmail()         { return email; }
    public double getWalletBalance() { return walletBalance; }
}
