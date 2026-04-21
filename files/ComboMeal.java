package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A combo meal composed of multiple MenuItems sold as a package.
 * Demonstrates: INHERITANCE (extends MenuItem)
 *               POLYMORPHISM (overrides getPrice() and getSummary())
 *               ENCAPSULATION (private combo-specific fields)
 */
public class ComboMeal extends MenuItem {

    // ENCAPSULATION: combo-specific private fields
    private List<MenuItem> comboItems;
    private double discountPercent;

    public ComboMeal(String itemId, String name, List<MenuItem> items, double discountPercent) {
        // INHERITANCE: calls parent constructor; price calculated from items
        super(itemId, name, calculateBasePrice(items), "COMBO",
              "Combo: " + buildDescription(items));
        this.comboItems = new ArrayList<>(items);
        this.discountPercent = discountPercent;
    }

    // Helper: sum up item prices
    private static double calculateBasePrice(List<MenuItem> items) {
        return items.stream().mapToDouble(MenuItem::getPrice).sum();
    }

    // Helper: build a description from item names
    private static String buildDescription(List<MenuItem> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i).getName());
            if (i < items.size() - 1) sb.append(" + ");
        }
        return sb.toString();
    }

    /**
     * POLYMORPHISM: overrides getPrice() to apply combo discount.
     * Caller does not need to know this is a combo — it just calls getPrice().
     */
    @Override
    public double getPrice() {
        double basePrice = calculateBasePrice(comboItems);
        return basePrice * (1 - discountPercent / 100.0);
    }

    /**
     * POLYMORPHISM: richer display for combos.
     */
    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-25s | COMBO    | $%-6.2f | (%.0f%% off)\n",
                getName(), getPrice(), discountPercent));
        for (MenuItem item : comboItems) {
            sb.append(String.format("    ↳ %s ($%.2f)\n", item.getName(), item.getPrice()));
        }
        return sb.toString().trim();
    }

    // ENCAPSULATION: combo-specific getters
    public List<MenuItem> getComboItems()        { return new ArrayList<>(comboItems); }
    public double getDiscountPercent()           { return discountPercent; }
    public double getSavings() {
        return calculateBasePrice(comboItems) - getPrice();
    }
}
