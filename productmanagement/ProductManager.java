package productmanagement;

import java.util.ArrayList;
import java.util.List;

public class ProductManager {

    private List<Product> inventory;
    private String storeName;

    public ProductManager(String storeName) {
        this.storeName = storeName;
        this.inventory = new ArrayList<>();
    }

    public void addProduct(Product product) {
        inventory.add(product);
        System.out.println("[+] Added: " + product.getName() + " (" + product.getCategory() + ")");
    }

    public void removeProduct(String productId) {
        inventory.removeIf(p -> p.getProductId().equals(productId));
        System.out.println("[-] Removed product with ID: " + productId);
    }

    // Search by name (case-insensitive)
    public Product findProduct(String name) {
        for (Product p : inventory) {
            if (p.getName().equalsIgnoreCase(name))
                return p;
        }
        System.out.println("Product not found: " + name);
        return null;
    }

    public void displayAllProducts() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║   " + storeName + " — Inventory        ║");
        System.out.println("╚══════════════════════════════════╝");
        if (inventory.isEmpty()) {
            System.out.println("  No products in inventory.");
            return;
        }
        for (Product p : inventory) {
            p.displayInfo();
        }
    }

    public void displayByCategory(String category) {
        System.out.println("\n--- Products in: " + category + " ---");
        for (Product p : inventory) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                p.displayInfo();
            }
        }
    }

    public int getTotalProducts() {
        return inventory.size();
    }

    public String getStoreName() {
        return storeName;
    }
}
