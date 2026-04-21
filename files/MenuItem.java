package com.fooddelivery.model;

/**
 * Represents a single food item on a restaurant's menu.
 * Demonstrates: ENCAPSULATION (private fields)
 *               POLYMORPHISM (implements Deliverable interface)
 *               ABSTRACTION (fulfills contract defined by Deliverable)
 */
public class MenuItem implements Deliverable {

    // ENCAPSULATION: private fields
    private String itemId;
    private String name;
    private double price;
    private String category;   // e.g., "FOOD", "DRINK", "DESSERT", "COMBO"
    private String description;
    private boolean available;

    public MenuItem(String itemId, String name, double price,
                    String category, String description) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category.toUpperCase();
        this.description = description;
        this.available = true;
    }

    // POLYMORPHISM: implementing Deliverable interface
    @Override
    public String getName()        { return name; }

    @Override
    public double getPrice()       { return price; }

    @Override
    public String getCategory()    { return category; }

    @Override
    public boolean isAvailable()   { return available; }

    /**
     * POLYMORPHISM: overrides the default getSummary() from interface
     * with a more detailed version.
     */
    @Override
    public String getSummary() {
        return String.format("  %-25s | %-8s | $%-6.2f | %s",
                name, category, price, description);
    }

    // ENCAPSULATION: controlled setters
    public String getItemId()                    { return itemId; }
    public String getDescription()               { return description; }
    public void setPrice(double price)           { this.price = Math.max(0, price); }
    public void setAvailable(boolean available)  { this.available = available; }
    public void setDescription(String desc)      { this.description = desc; }

    @Override
    public String toString() {
        return getSummary();
    }
}
