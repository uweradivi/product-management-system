
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  PRODUCT MANAGEMENT SYSTEM — Collections Demo    ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        ProductManager store = new ProductManager("TechMart Store");

        Customer alice = new Customer("C001", "Alice Nkurunziza", "alice@email.com", 5000.00);
        Customer bob = new Customer("C002", "Bob Mugisha", "bob@email.com", 80.00);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  COLLECTION 1: List<Product>                     ║");
        System.out.println("║  Relationship: One store → Many products         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ADD to List
        System.out.println("\n--- Adding products to List ---");
        Electronics laptop = new Electronics("E001", "Laptop Pro 15", 1200.00, 10, 2);
        Electronics phone = new Electronics("E002", "SmartPhone X", 799.00, 25, 1);
        Electronics tablet = new Electronics("E003", "Tablet Y10", 450.00, 15, 1);
        Food rice = new Food("F001", "Basmati Rice 5kg", 12.50, 50, "2026-06-30");
        Food milk = new Food("F002", "Fresh Milk 1L", 1.80, 100, "2025-05-10");
        Clothing shirt = new Clothing("C001", "Polo Shirt", 25.00, 50, "L", "Cotton");
        Clothing jacket = new Clothing("C002", "Winter Jacket", 89.99, 30, "XL", "Polyester");

        store.addProduct(laptop);
        store.addProduct(phone);
        store.addProduct(tablet);
        store.addProduct(rice);
        store.addProduct(milk);
        store.addProduct(shirt);
        store.addProduct(jacket);
        store.displayAllProducts();

        System.out.println("\n--- Retrieving Electronics from List ---");
        List<Product> electronics = store.getProductsByCategory("Electronics");
        System.out.println("Electronics found: " + electronics.size());
        for (Product p : electronics) {
            System.out.println("  > " + p.getName() + " — $" + p.getPrice());
        }

        System.out.println("\n--- Removing Tablet from List ---");
        store.removeProduct("E003");
        System.out.println("Products after removal: " + store.getTotalProducts());

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  COLLECTION 2: Set<String>                       ║");
        System.out.println("║  Relationship: Unique categories (no duplicates) ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // RETRIEVE from Set — all unique categories
        System.out.println("\n--- All unique categories in store (Set) ---");
        Set<String> allCategories = store.getAllCategories();
        System.out.println("Total unique categories: " + allCategories.size());
        for (String cat : allCategories) {
            System.out.println("  > " + cat);
        }

        System.out.println("\n--- Checking if categories exist (Set.contains) ---");
        System.out.println("Has 'Electronics'   : " + store.hasCategory("Electronics"));
        System.out.println("Has 'Food & Grocery': " + store.hasCategory("Food & Grocery"));
        System.out.println("Has 'Toys'          : " + store.hasCategory("Toys"));

        System.out.println("\n--- Adding more Electronics (Set stays unique) ---");
        Electronics camera = new Electronics("E004", "Digital Camera", 350.00, 20, 1);
        store.addProduct(camera);
        System.out.println("Categories after adding camera: " + store.getAllCategories());
        System.out
                .println("(Still " + store.getAllCategories().size() + " unique categories — Set blocked duplicate!)");
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  COLLECTION 3: Map<String, List<Order>>          ║");
        System.out.println("║  Relationship: Customer ID → order history       ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ADD to Map via purchases (order recorded automatically)
        System.out.println("\n--- Alice makes purchases (orders added to Map) ---");
        alice.showProfile();
        try {
            alice.purchase(laptop, 1, store);
            alice.purchase(shirt, 2, store);
            alice.purchase(rice, 3, store);
        } catch (InsufficientBalanceException | OutOfStockException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
        }

        System.out.println("\n--- Bob makes purchases ---");
        bob.showProfile();
        try {
            bob.purchase(shirt, 1, store);
            bob.purchase(milk, 2, store);
        } catch (InsufficientBalanceException | OutOfStockException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
        }
        System.out.println();
        store.showOrderHistory("C001", "Alice Nkurunziza");
        store.showOrderHistory("C002", "Bob Mugisha");

        // RETRIEVE from Map — full order summary (all customers)
        store.showAllOrderSummary();

        // REMOVE from Map — cancel an order
        System.out.println("\n--- Removing order ORD001 from Alice's history ---");
        store.removeOrder("C001", "ORD001");
        store.showOrderHistory("C001", "Alice Nkurunziza");
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  EXCEPTION HANDLING (Day 7 still works)          ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        System.out.println("\n--- Test: OutOfStockException ---");
        try {
            rice.sell(9999);
        } catch (OutOfStockException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
        }

        System.out.println("\n--- Test: InsufficientBalanceException ---");
        try {
            bob.purchase(laptop, 1, store);
        } catch (InsufficientBalanceException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
        }

        System.out.println("\n--- Test: ProductNotFoundException ---");
        try {
            store.findProduct("Invisible Product");
        } catch (ProductNotFoundException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
        }

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  COLLECTIONS SUMMARY                             ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  List<Product>           → " + store.getTotalProducts() + " products in inventory  ║");
        System.out.println(
                "║  Set<String>             → " + store.getAllCategories().size() + " unique categories       ║");
        System.out.println("║  Map<String,List<Order>> → 2 customers tracked       ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  All collections demonstrated:                   ║");
        System.out.println("║  ADD, RETRIEVE, REMOVE operations ✓              ║");
        System.out.println("║  Program ran without crashing ✓                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("[System Shutdown — Thank you!]");
    }
}
