package com.fooddelivery.service;

import com.fooddelivery.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Central service that manages the platform: restaurants, customers, drivers, orders.
 * Demonstrates: ABSTRACTION (hides all management logic behind clean method calls)
 *               ENCAPSULATION (private lists, access only through methods)
 *               RELATIONSHIPS (coordinates all entities together)
 */
public class DeliveryService {

    // ENCAPSULATION: private data stores
    private String platformName;
    private List<Restaurant>     restaurants;
    private List<Customer>       customers;
    private List<DeliveryDriver> drivers;
    private List<Order>          allOrders;

    public DeliveryService(String platformName) {
        this.platformName = platformName;
        this.restaurants  = new ArrayList<>();
        this.customers    = new ArrayList<>();
        this.drivers      = new ArrayList<>();
        this.allOrders    = new ArrayList<>();
        System.out.println("🚀 " + platformName + " platform initialized.\n");
    }

    // ── Registration ─────────────────────────────────────────────────────────

    public void registerRestaurant(Restaurant r) {
        restaurants.add(r);
        System.out.println("🏪 Restaurant registered: " + r.getName());
    }

    public void registerCustomer(Customer c) {
        customers.add(c);
        System.out.println("👤 Customer registered: " + c.getName());
    }

    public void registerDriver(DeliveryDriver d) {
        drivers.add(d);
        System.out.println("🚴 Driver registered: " + d.getName() + " (" + d.getVehicleType() + ")");
    }

    // ── Lookups ───────────────────────────────────────────────────────────────

    public Optional<Restaurant> findRestaurantById(String id) {
        return restaurants.stream().filter(r -> r.getRestaurantId().equals(id)).findFirst();
    }

    public Optional<Customer> findCustomerById(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Optional<DeliveryDriver> findAvailableDriver() {
        return drivers.stream().filter(DeliveryDriver::isAvailable).findFirst();
    }

    // ── Order Workflow ────────────────────────────────────────────────────────

    /**
     * Full order dispatch: confirm → prepare → assign driver → deliver.
     * ABSTRACTION: caller just says "dispatch" and the system handles the rest.
     */
    public void dispatchOrder(Order order) {
        System.out.println("\n📋 Dispatching order: " + order.getOrderId());
        order.confirm();
        order.startPreparing();
        order.markReadyForPickup();

        Optional<DeliveryDriver> driverOpt = findAvailableDriver();
        if (driverOpt.isPresent()) {
            DeliveryDriver driver = driverOpt.get();
            driver.acceptDelivery(order);
            driver.completeDelivery(order);
        } else {
            System.out.println("⚠️  No drivers available right now for order: " + order.getOrderId());
        }
        allOrders.add(order);
    }

    // ── Reporting ─────────────────────────────────────────────────────────────

    public void printPlatformSummary() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║   📊 " + platformName + " SUMMARY");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  Restaurants : " + restaurants.size());
        System.out.println("║  Customers   : " + customers.size());
        System.out.println("║  Drivers     : " + drivers.size());
        System.out.println("║  Total Orders: " + allOrders.size());

        long delivered = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        double revenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount).sum();

        System.out.printf("║  Delivered   : %d%n", delivered);
        System.out.printf("║  Revenue     : $%.2f%n", revenue);
        System.out.println("╚════════════════════════════════════╝\n");
    }

    public void listAllRestaurants() {
        System.out.println("\n🏪 ALL RESTAURANTS ON PLATFORM:");
        // POLYMORPHISM: calls displayInfo() on each — could be extended with subtypes
        restaurants.forEach(Restaurant::displayInfo);
    }

    public void listAllDrivers() {
        System.out.println("\n🚴 ALL DRIVERS:");
        // POLYMORPHISM: each Person subclass has its own displayInfo()
        drivers.forEach(Person::displayInfo);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public List<Restaurant>     getRestaurants() { return new ArrayList<>(restaurants); }
    public List<Customer>       getCustomers()   { return new ArrayList<>(customers); }
    public List<DeliveryDriver> getDrivers()     { return new ArrayList<>(drivers); }
    public List<Order>          getAllOrders()    { return new ArrayList<>(allOrders); }
    public String               getPlatformName(){ return platformName; }
}
