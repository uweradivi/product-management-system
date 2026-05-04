package com.fooddelivery.model;

/**
 * Interface defining what it means to be a deliverable item.
 * Demonstrates: ABSTRACTION (interface = pure abstraction)
 *               POLYMORPHISM (multiple classes implement this differently)
 */
public interface Deliverable {

    /**
     * Get the name/label of the deliverable.
     */
    String getName();

    /**
     * Get the price of the deliverable.
     */
    double getPrice();

    /**
     * Get the category (e.g., FOOD, DRINK, COMBO).
     */
    String getCategory();

    /**
     * Check if the item is currently available.
     */
    boolean isAvailable();

    /**
     * Returns a formatted summary for receipts/menus.
     */
    default String getSummary() {
        return String.format("%-25s [%s] $%.2f %s",
                getName(),
                getCategory(),
                getPrice(),
                isAvailable() ? "" : "(Unavailable)");
    }
}
