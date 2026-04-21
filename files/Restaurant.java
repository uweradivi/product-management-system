package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a restaurant on the platform.
 * Demonstrates: ENCAPSULATION (private fields, controlled access)
 *               ABSTRACTION (hides menu complexity behind clean methods)
 *               RELATIONSHIPS (has-a: owns MenuItems)
 */
public class Restaurant {

    // ENCAPSULATION: all fields are private
    private String restaurantId;
    private String name;
    private String cuisineType;
    private String address;
    private double rating;
    private boolean isOpen;
    private List<MenuItem> menu;

    public Restaurant(String restaurantId, String name,
                      String cuisineType, String address) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.cuisineType = cuisineType;
        this.address = address;
        this.rating = 4.0;
        this.isOpen = true;
        this.menu = new ArrayList<>();
    }

    /**
     * Add an item to this restaurant's menu.
     */
    public void addMenuItem(MenuItem item) {
        menu.add(item);
        System.out.println("🍽️  Added '" + item.getName() + "' to " + name + "'s menu.");
    }

    /**
     * Remove a menu item by ID.
     */
    public boolean removeMenuItem(String itemId) {
        return menu.removeIf(item -> item.getItemId().equals(itemId));
    }

    /**
     * Search menu by name (partial match, case-insensitive).
     */
    public List<MenuItem> searchMenu(String keyword) {
        List<MenuItem> results = new ArrayList<>();
        for (MenuItem item : menu) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Get all items in a specific category.
     */
    public List<MenuItem> getMenuByCategory(String category) {
        List<MenuItem> results = new ArrayList<>();
        for (MenuItem item : menu) {
            if (item.getCategory().equalsIgnoreCase(category) && item.isAvailable()) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Display the full menu in a formatted way.
     */
    public void displayMenu() {
        System.out.println("\n====================================================");
        System.out.println("  🏪 " + name + " — " + cuisineType + " Cuisine");
        System.out.println("  📍 " + address + " | ⭐ " + rating + " | "
                + (isOpen ? "🟢 OPEN" : "🔴 CLOSED"));
        System.out.println("====================================================");
        if (menu.isEmpty()) {
            System.out.println("  No items on the menu yet.");
        } else {
            System.out.printf("  %-25s | %-8s | %-8s | %s%n",
                    "Item Name", "Category", "Price", "Description");
            System.out.println("  " + "-".repeat(72));
            // POLYMORPHISM: calls getSummary() — works for MenuItem AND ComboMeal
            for (MenuItem item : menu) {
                System.out.println(item.getSummary());
            }
        }
        System.out.println("====================================================\n");
    }

    public void displayInfo() {
        System.out.println("🏪 Restaurant: " + name + " | " + cuisineType
                + " | Rating: " + rating + " | " + (isOpen ? "Open" : "Closed"));
    }

    // ENCAPSULATION: getters and setters
    public String getRestaurantId()              { return restaurantId; }
    public String getName()                      { return name; }
    public String getCuisineType()               { return cuisineType; }
    public String getAddress()                   { return address; }
    public double getRating()                    { return rating; }
    public boolean isOpen()                      { return isOpen; }
    public List<MenuItem> getMenu()              { return new ArrayList<>(menu); }

    public void setRating(double rating)         { this.rating = Math.max(1.0, Math.min(5.0, rating)); }
    public void setOpen(boolean open)            { this.isOpen = open; }
    public void setAddress(String address)       { this.address = address; }
}
