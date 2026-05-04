package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a food delivery order — the core entity of the system.
 * Demonstrates: ENCAPSULATION (private fields, controlled state changes)
 *               RELATIONSHIPS (links Customer ↔ Restaurant ↔ Driver ↔ MenuItems)
 *               ABSTRACTION (exposes simple methods that hide complex logic)
 *               POLYMORPHISM (iterates Deliverable items — works for MenuItem & ComboMeal)
 */
public class Order {

    private static final double DELIVERY_FEE = 2.50;
    private static final double TAX_RATE     = 0.08; // 8%

    // ENCAPSULATION: all private
    private String orderId;
    private Customer customer;
    private Restaurant restaurant;
    private DeliveryDriver assignedDriver;       // RELATIONSHIP: has-a Driver
    private List<MenuItem> items;                // RELATIONSHIP: has-many MenuItems
    private OrderStatus status;
    private LocalDateTime orderTime;
    private LocalDateTime deliveredTime;
    private String specialInstructions;

    public Order(String orderId, Customer customer, Restaurant restaurant) {
        this.orderId = orderId;
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.orderTime = LocalDateTime.now();
    }

    // ── Item Management ─────────────────────────────────────────────────────

    /**
     * Add a menu item to this order (if available).
     */
    public boolean addItem(MenuItem item) {
        if (!item.isAvailable()) {
            System.out.println("❌ '" + item.getName() + "' is currently unavailable.");
            return false;
        }
        items.add(item);
        System.out.println("  ➕ Added: " + item.getName() + " ($" + item.getPrice() + ")");
        return true;
    }

    /**
     * Remove an item from this order by name.
     */
    public boolean removeItem(String itemName) {
        return items.removeIf(i -> i.getName().equalsIgnoreCase(itemName));
    }

    // ── Pricing ─────────────────────────────────────────────────────────────

    /**
     * POLYMORPHISM: iterates as Deliverable — works for MenuItem AND ComboMeal.
     */
    public double getSubtotal() {
        double total = 0;
        for (Deliverable item : items) {   // uses Deliverable interface polymorphically
            total += item.getPrice();
        }
        return total;
    }

    public double getTax()          { return getSubtotal() * TAX_RATE; }
    public double getDeliveryFee()  { return DELIVERY_FEE; }
    public double getTotalAmount()  { return getSubtotal() + getTax() + getDeliveryFee(); }

    // ── Status Lifecycle ────────────────────────────────────────────────────

    public void confirm() {
        if (items.isEmpty()) {
            System.out.println("❌ Cannot confirm an empty order.");
            return;
        }
        status = OrderStatus.CONFIRMED;
        System.out.println("✅ Order " + orderId + " confirmed by " + restaurant.getName());
    }

    public void startPreparing() {
        status = OrderStatus.PREPARING;
        System.out.println("👨‍🍳 Restaurant is preparing order " + orderId);
    }

    public void markReadyForPickup() {
        status = OrderStatus.READY_FOR_PICKUP;
        System.out.println("📦 Order " + orderId + " is ready for driver pickup");
    }

    public void assignDriver(DeliveryDriver driver) {
        this.assignedDriver = driver;
        status = OrderStatus.OUT_FOR_DELIVERY;
    }

    public void markDelivered() {
        status = OrderStatus.DELIVERED;
        deliveredTime = LocalDateTime.now();
    }

    public void cancel() {
        if (status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED) {
            System.out.println("❌ Cannot cancel order that is already out or delivered.");
            return;
        }
        status = OrderStatus.CANCELLED;
        System.out.println("❌ Order " + orderId + " has been cancelled.");
    }

    // ── Display ──────────────────────────────────────────────────────────────

    public void printReceipt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║        🧾 ORDER RECEIPT              ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Order ID : %-24s ║%n", orderId);
        System.out.printf("║  Customer : %-24s ║%n", customer.getName());
        System.out.printf("║  Restaurant: %-23s ║%n", restaurant.getName());
        System.out.printf("║  Time     : %-24s ║%n", orderTime.format(fmt));
        System.out.printf("║  Status   : %-24s ║%n", status.name());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  ITEMS ORDERED:                      ║");
        // POLYMORPHISM: works for MenuItem and ComboMeal via Deliverable
        for (Deliverable item : items) {
            System.out.printf("║    %-22s $%6.2f   ║%n",
                    item.getName(), item.getPrice());
        }
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Subtotal  :              $%6.2f   ║%n", getSubtotal());
        System.out.printf("║  Tax (8%%) :              $%6.2f   ║%n", getTax());
        System.out.printf("║  Delivery  :              $%6.2f   ║%n", getDeliveryFee());
        System.out.println("║                            ───────   ║");
        System.out.printf("║  TOTAL     :              $%6.2f   ║%n", getTotalAmount());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Driver    : %-24s ║%n",
                assignedDriver != null ? assignedDriver.getName() : "Not assigned yet");
        System.out.printf("║  Address   : %-24s ║%n",
                customer.getDeliveryAddress().length() > 23
                        ? customer.getDeliveryAddress().substring(0, 23)
                        : customer.getDeliveryAddress());
        System.out.println("╚══════════════════════════════════════╝\n");
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getOrderId()                   { return orderId; }
    public Customer getCustomer()                { return customer; }
    public Restaurant getRestaurant()            { return restaurant; }
    public DeliveryDriver getAssignedDriver()    { return assignedDriver; }
    public List<MenuItem> getItems()             { return new ArrayList<>(items); }
    public OrderStatus getStatus()               { return status; }
    public LocalDateTime getOrderTime()          { return orderTime; }
    public String getSpecialInstructions()       { return specialInstructions; }
    public void setSpecialInstructions(String s) { this.specialInstructions = s; }
}
