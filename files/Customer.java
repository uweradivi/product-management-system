package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer who places food orders.
 * Demonstrates: INHERITANCE (extends Person)
 *               ENCAPSULATION (private fields)
 *               POLYMORPHISM (overrides displayInfo())
 */
public class Customer extends Person {

    // ENCAPSULATION: customer-specific private fields
    private String deliveryAddress;
    private List<Order> orderHistory;

    public Customer(String id, String name, String email,
                    String phoneNumber, String deliveryAddress) {
        super(id, name, email, phoneNumber); // INHERITANCE: calling parent constructor
        this.deliveryAddress = deliveryAddress;
        this.orderHistory = new ArrayList<>();
    }

    // POLYMORPHISM: specific role for this subclass
    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    // POLYMORPHISM: customer-specific display
    @Override
    public void displayInfo() {
        System.out.println("========== CUSTOMER PROFILE ==========");
        System.out.println("  ID      : " + getId());
        System.out.println("  Name    : " + getName());
        System.out.println("  Email   : " + getEmail());
        System.out.println("  Phone   : " + getPhoneNumber());
        System.out.println("  Address : " + deliveryAddress);
        System.out.println("  Orders  : " + orderHistory.size() + " total");
        System.out.println("======================================");
    }

    /**
     * Place a new order and add it to this customer's history.
     */
    public Order placeOrder(Restaurant restaurant) {
        String orderId = "ORD-" + getId() + "-" + (orderHistory.size() + 1);
        Order order = new Order(orderId, this, restaurant);
        orderHistory.add(order);
        System.out.println("✅ Order placed: " + orderId + " at " + restaurant.getName());
        return order;
    }

    // ENCAPSULATION: controlled access
    public String getDeliveryAddress()           { return deliveryAddress; }
    public void setDeliveryAddress(String addr)  { this.deliveryAddress = addr; }
    public List<Order> getOrderHistory()         { return new ArrayList<>(orderHistory); }
}
