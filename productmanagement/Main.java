package productmanagement;

public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║    PRODUCT MANAGEMENT SYSTEM — OOP Demo  ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        ProductManager store = new ProductManager("TechMart Store");

        Electronics laptop = new Electronics("E001", "Laptop Pro 15", 1200.00, 10, 2);
        Electronics phone = new Electronics("E002", "SmartPhone X", 799.00, 25, 1);
        Food rice = new Food("F001", "Basmati Rice 5kg", 12.50, 100, "2026-06-30");
        Food milk = new Food("F002", "Fresh Milk 1L", 1.80, 200, "2025-05-10");
        Clothing shirt = new Clothing("C001", "Polo Shirt", 25.00, 50, "L", "Cotton");
        Clothing jacket = new Clothing("C002", "Winter Jacket", 89.99, 30, "XL", "Polyester");

        System.out.println("=== ADDING PRODUCTS TO INVENTORY ===");
        store.addProduct(laptop);
        store.addProduct(phone);
        store.addProduct(rice);
        store.addProduct(milk);
        store.addProduct(shirt);
        store.addProduct(jacket);

        store.displayAllProducts();

        System.out.println("\n=== PRODUCT-SPECIFIC FEATURES ===");
        laptop.showWarranty();
        rice.checkExpiry();
        shirt.showDetails();
        System.out.println("\n=== CUSTOMER PURCHASES ===");
        Customer alice = new Customer("CU001", "Alice Nkurunziza", "alice@email.com", 2000.00);
        Customer bob = new Customer("CU002", "Bob Mugisha", "bob@email.com", 15.00);

        alice.showProfile();
        alice.purchase(laptop, 1);
        alice.purchase(phone, 1);

        System.out.println();
        bob.showProfile();
        bob.purchase(jacket, 1);
        bob.purchase(rice, 2);

        System.out.println("\n=== RESTOCKING ===");
        laptop.restock(5);
        System.out.println("\n=== SEARCH ===");
        Product found = store.findProduct("Polo Shirt");
        if (found != null) {
            System.out.println("Found product: " + found.getName() + " in " + found.getCategory());
        }
        System.out.println("\n=== STORE SUMMARY ===");
        System.out.println("Store    : " + store.getStoreName());
        System.out.println("Products : " + store.getTotalProducts() + " items in inventory");
        System.out.println("\n[System Shutdown — Thank you!]");
    }
}
