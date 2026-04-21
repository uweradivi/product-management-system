package com.fooddelivery.model;

/**
 * Represents a delivery driver who delivers orders.
 * Demonstrates: INHERITANCE (extends Person)
 *               ENCAPSULATION (private fields + getters/setters)
 *               POLYMORPHISM (overrides getRole() and displayInfo())
 */
public class DeliveryDriver extends Person {

    // ENCAPSULATION: driver-specific private fields
    private String vehicleType;
    private boolean isAvailable;
    private int totalDeliveries;
    private double rating;

    public DeliveryDriver(String id, String name, String email,
                          String phoneNumber, String vehicleType) {
        super(id, name, email, phoneNumber); // INHERITANCE: parent constructor
        this.vehicleType = vehicleType;
        this.isAvailable = true;
        this.totalDeliveries = 0;
        this.rating = 5.0;
    }

    // POLYMORPHISM: overrides abstract method from Person
    @Override
    public String getRole() {
        return "DRIVER";
    }

    // POLYMORPHISM: driver-specific display, different from Customer & Restaurant
    @Override
    public void displayInfo() {
        System.out.println("========== DRIVER PROFILE ==========");
        System.out.println("  ID          : " + getId());
        System.out.println("  Name        : " + getName());
        System.out.println("  Phone       : " + getPhoneNumber());
        System.out.println("  Vehicle     : " + vehicleType);
        System.out.println("  Available   : " + (isAvailable ? "Yes" : "No (on delivery)"));
        System.out.println("  Deliveries  : " + totalDeliveries);
        System.out.println("  Rating      : " + rating + " ⭐");
        System.out.println("=====================================");
    }

    /**
     * Assign this driver to deliver an order.
     */
    public boolean acceptDelivery(Order order) {
        if (!isAvailable) {
            System.out.println("❌ Driver " + getName() + " is not available.");
            return false;
        }
        isAvailable = false;
        order.assignDriver(this);
        System.out.println("🚴 Driver " + getName() + " accepted order: " + order.getOrderId());
        return true;
    }

    /**
     * Mark delivery as complete.
     */
    public void completeDelivery(Order order) {
        order.markDelivered();
        isAvailable = true;
        totalDeliveries++;
        System.out.println("📦 Delivery complete by " + getName() + " for order: " + order.getOrderId());
    }

    // ENCAPSULATION: controlled access
    public String getVehicleType()               { return vehicleType; }
    public boolean isAvailable()                 { return isAvailable; }
    public int getTotalDeliveries()              { return totalDeliveries; }
    public double getRating()                    { return rating; }
    public void setRating(double rating)         { this.rating = Math.max(1.0, Math.min(5.0, rating)); }
}
