package com.fooddelivery;

import com.fooddelivery.model.*;
import com.fooddelivery.service.DeliveryService;

import java.util.Arrays;

/**
 * Main entry point for the Food Delivery System.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 * ─────────────────────────────────────────────────────────────────────
 * 1. CLASSES & OBJECTS   → Person, Customer, DeliveryDriver, Restaurant,
 *                          MenuItem, ComboMeal, Order, DeliveryService
 *
 * 2. ENCAPSULATION       → All fields private; accessed via getters/setters
 *
 * 3. ABSTRACTION         → Abstract class Person (getRole, displayInfo)
 *                          Interface Deliverable (getName, getPrice, ...)
 *                          DeliveryService hides all dispatch complexity
 *
 * 4. INHERITANCE         → Customer   extends Person
 *                          DeliveryDriver extends Person
 *                          ComboMeal   extends MenuItem (which implements Deliverable)
 *
 * 5. POLYMORPHISM        → displayInfo() behaves differently per Person subtype
 *                          getPrice() behaves differently for ComboMeal vs MenuItem
 *                          getSummary() behaves differently per Deliverable type
 *                          Order iterates as Deliverable — works for all subtypes
 * ─────────────────────────────────────────────────────────────────────
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       🍔 FOOD DELIVERY SYSTEM — OOP DEMO             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        // ── 1. Initialize Platform ────────────────────────────────────────────
        DeliveryService platform = new DeliveryService("QuickBite");

        // ── 2. Create Restaurants ─────────────────────────────────────────────
        Restaurant burgerHouse = new Restaurant("R001", "Burger House", "American", "12 Main St");
        Restaurant pizzaPalace = new Restaurant("R002", "Pizza Palace", "Italian",  "45 Olive Ave");

        // ── 3. Build Menus with MenuItems ─────────────────────────────────────
        MenuItem burger    = new MenuItem("M001", "Classic Burger",   8.99,  "FOOD",   "Beef, lettuce, tomato");
        MenuItem fries     = new MenuItem("M002", "Crispy Fries",     3.49,  "FOOD",   "Golden fried potatoes");
        MenuItem cola      = new MenuItem("M003", "Cola",             1.99,  "DRINK",  "Chilled soft drink");
        MenuItem milkshake = new MenuItem("M004", "Vanilla Shake",    4.99,  "DRINK",  "Creamy milkshake");
        MenuItem cheeseBurger = new MenuItem("M005", "Cheese Burger", 9.99,  "FOOD",   "Double cheese patty");

        burgerHouse.addMenuItem(burger);
        burgerHouse.addMenuItem(fries);
        burgerHouse.addMenuItem(cola);
        burgerHouse.addMenuItem(milkshake);
        burgerHouse.addMenuItem(cheeseBurger);

        // ── INHERITANCE + POLYMORPHISM: ComboMeal extends MenuItem ────────────
        ComboMeal burgerCombo = new ComboMeal(
                "C001",
                "Burger Meal Deal",
                Arrays.asList(burger, fries, cola),
                15.0    // 15% discount
        );
        burgerHouse.addMenuItem(burgerCombo);

        MenuItem margherita = new MenuItem("M006", "Margherita Pizza", 11.99, "FOOD",  "Classic tomato & mozzarella");
        MenuItem pepperoni  = new MenuItem("M007", "Pepperoni Pizza",  13.99, "FOOD",  "Spicy pepperoni slices");
        MenuItem garlic     = new MenuItem("M008", "Garlic Bread",      3.99, "FOOD",  "Toasted herb garlic bread");

        pizzaPalace.addMenuItem(margherita);
        pizzaPalace.addMenuItem(pepperoni);
        pizzaPalace.addMenuItem(garlic);

        // ── 4. Register with Platform ─────────────────────────────────────────
        System.out.println();
        platform.registerRestaurant(burgerHouse);
        platform.registerRestaurant(pizzaPalace);

        // ── 5. Create Customers (INHERITANCE: Customer extends Person) ─────────
        Customer alice = new Customer("C001", "Alice Mugisha",   "alice@email.com",  "0788001001", "22 Kigali Heights");
        Customer bob   = new Customer("C002", "Bob Nkurunziza",  "bob@email.com",    "0788002002", "5 Nyamirambo Rd");

        System.out.println();
        platform.registerCustomer(alice);
        platform.registerCustomer(bob);

        // ── 6. Create Drivers (INHERITANCE: DeliveryDriver extends Person) ────
        DeliveryDriver driver1 = new DeliveryDriver("D001", "James Uwimana", "james@email.com", "0788003003", "Motorcycle");
        DeliveryDriver driver2 = new DeliveryDriver("D002", "Marie Ingabire", "marie@email.com", "0788004004", "Bicycle");

        System.out.println();
        platform.registerDriver(driver1);
        platform.registerDriver(driver2);

        // ── 7. Display Menus ──────────────────────────────────────────────────
        System.out.println();
        burgerHouse.displayMenu();
        pizzaPalace.displayMenu();

        // ── 8. POLYMORPHISM: displayInfo() behaves differently per Person type ─
        System.out.println("\n── POLYMORPHISM DEMO: displayInfo() on different Person types ──");
        alice.displayInfo();     // Customer version
        driver1.displayInfo();   // DeliveryDriver version

        // ── 9. Alice places Order 1 (with ComboMeal + extra item) ────────────
        System.out.println("\n── ALICE PLACES ORDER ──────────────────────────────────────────");
        Order order1 = alice.placeOrder(burgerHouse);
        order1.addItem(burgerCombo);    // POLYMORPHISM: ComboMeal as Deliverable
        order1.addItem(milkshake);      // MenuItem as Deliverable
        order1.setSpecialInstructions("Extra ketchup please!");

        // ── 10. POLYMORPHISM: getPrice() differs for ComboMeal vs MenuItem ────
        System.out.println("\nCOMBO PRICE (after 15% discount): $" + String.format("%.2f", burgerCombo.getPrice()));
        System.out.println("SAVINGS on combo: $" + String.format("%.2f", burgerCombo.getSavings()));

        // ── 11. Dispatch Order (service abstracts the workflow) ───────────────
        platform.dispatchOrder(order1);
        order1.printReceipt();

        // ── 12. Bob places Order 2 ────────────────────────────────────────────
        System.out.println("\n── BOB PLACES ORDER ────────────────────────────────────────────");
        Order order2 = bob.placeOrder(pizzaPalace);
        order2.addItem(margherita);
        order2.addItem(garlic);
        platform.dispatchOrder(order2);
        order2.printReceipt();

        // ── 13. Platform Summary ──────────────────────────────────────────────
        platform.printPlatformSummary();
        platform.listAllRestaurants();
        platform.listAllDrivers();

        // ── 14. Demonstrate Customer Order History ────────────────────────────
        System.out.println("\n── ALICE'S ORDER HISTORY ───────────────────────────────────────");
        alice.getOrderHistory().forEach(o ->
            System.out.println("  Order " + o.getOrderId()
                    + " | Status: " + o.getStatus().name()
                    + " | Total: $" + String.format("%.2f", o.getTotalAmount()))
        );

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              ✅ DEMO COMPLETE                        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
