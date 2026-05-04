package com.fooddelivery.model;

/**
 * Enum representing all possible states of an order lifecycle.
 * Demonstrates: ABSTRACTION (hides status logic as typed constants)
 */
public enum OrderStatus {
    PENDING("⏳ Pending — Waiting for restaurant to confirm"),
    CONFIRMED("✅ Confirmed — Restaurant is preparing your order"),
    PREPARING("👨‍🍳 Preparing — Your food is being cooked"),
    READY_FOR_PICKUP("📦 Ready — Waiting for driver pickup"),
    OUT_FOR_DELIVERY("🚴 Out for Delivery — Driver is on the way"),
    DELIVERED("🎉 Delivered — Enjoy your meal!"),
    CANCELLED("❌ Cancelled — Order was cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
