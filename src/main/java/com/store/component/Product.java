package com.store.component;

import java.io.Serializable;

/**
 * Abstract base class for all product types.
 * Implements Serializable so products can be saved/loaded from files.
 */
public abstract class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productId;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(String productId, String name, double price, int stockQuantity) {
        if (price <= 0) throw new InvalidPriceException(price);
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.productId     = productId;
        this.name          = name;
        this.price         = price;
        this.stockQuantity = stockQuantity;
    }

    public abstract String getCategory();
    public abstract double applyDiscount();
    public abstract String toCsv();

    /** Extra fields printed by each subclass before closing border */
    protected void printExtraInfo() {}

    public final void displayInfo() {
        System.out.println("  +-----------------------------------------");
        System.out.println("  | ID       : " + productId);
        System.out.println("  | Name     : " + name);
        System.out.println("  | Category : " + getCategory());
        System.out.printf ("  | Price    : $%.2f%n", price);
        System.out.printf ("  | Discount : -$%.2f%n", applyDiscount());
        System.out.printf ("  | Final    : $%.2f%n", price - applyDiscount());
        System.out.println("  | Stock    : " + stockQuantity + " units");
        printExtraInfo();
        System.out.println("  +-----------------------------------------");
    }

    public void restock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Restock quantity must be positive.");
        this.stockQuantity += quantity;
        System.out.println("  [OK] Restocked '" + name + "' by " + quantity
                + " units. New stock: " + stockQuantity);
    }

    public void sell(int quantity) {
        if (quantity > stockQuantity) {
            throw new OutOfStockException(name, quantity, stockQuantity);
        }
        stockQuantity -= quantity;
        System.out.println("  [OK] Sold " + quantity + " x " + name
                + ". Remaining stock: " + stockQuantity);
    }

    // Getters & Setters
    public String getProductId()     { return productId; }
    public String getName()          { return name; }
    public double getPrice()         { return price; }
    public int    getStockQuantity() { return stockQuantity; }

    public void setPrice(double price) {
        if (price <= 0) throw new InvalidPriceException(price);
        this.price = price;
    }
    public void setStockQuantity(int qty) {
        if (qty < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.stockQuantity = qty;
    }
    public void setName(String name) { this.name = name; }
}
