
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductManager {

    private List<Product> inventory;

    private Set<String> categories;

    private Map<String, List<Order>> orderHistory;

    private String storeName;
    private int orderCounter;

    public ProductManager(String storeName) {
        if (storeName == null || storeName.trim().isEmpty())
            throw new NullPointerException("Store name cannot be null or empty.");

        this.storeName = storeName;
        this.inventory = new ArrayList<>(); // List implementation
        this.categories = new HashSet<>(); // Set implementation
        this.orderHistory = new HashMap<>(); // Map implementation
        this.orderCounter = 1;
    }

    public void addProduct(Product product) {
        if (product == null)
            throw new NullPointerException("Cannot add a null product.");

        inventory.add(product);

        categories.add(product.getCategory());

        System.out.println("[+] Added to inventory  : " + product.getName()
                + " (" + product.getCategory() + ")");
        System.out.println("    Categories in store  : " + categories);
    }

    public void removeProduct(String productId) {
        Product toRemove = null;
        for (Product p : inventory) {
            if (p.getProductId().equals(productId)) {
                toRemove = p;
                break;
            }
        }
        if (toRemove != null) {
            inventory.remove(toRemove);
            System.out.println("[-] Removed from inventory: " + toRemove.getName());
        } else {
            throw new ProductNotFoundException(productId);
        }
    }

    public Product findProduct(String name) {
        for (Product p : inventory) {
            if (p.getName().equalsIgnoreCase(name))
                return p;
        }
        throw new ProductNotFoundException(name);
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product p : inventory) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                result.add(p);
            }
        }
        return result;
    }

    public boolean hasCategory(String category) {
        return categories.contains(category);
    }

    public Set<String> getAllCategories() {
        return categories;
    }

    public void recordOrder(String customerId, String customerName,
            String productName, int quantity, double totalCost) {

        String orderId = "ORD" + String.format("%03d", orderCounter++);
        Order order = new Order(orderId, customerName, productName, quantity, totalCost);

        // If this customer has no orders yet, create a new list for them
        // getOrDefault returns existing list OR a new empty list
        List<Order> customerOrders = orderHistory.getOrDefault(customerId, new ArrayList<>());

        // Add the new order to their list
        customerOrders.add(order);

        // Put the updated list back into the Map
        orderHistory.put(customerId, customerOrders);

        System.out.println("[ORDER RECORDED] " + customerName
                + " → " + orderId + " (" + productName + " x" + quantity + ")");
    }

    public void showOrderHistory(String customerId, String customerName) {
        System.out.println("\n=== Order History for " + customerName + " ===");

        List<Order> orders = orderHistory.get(customerId);

        if (orders == null || orders.isEmpty()) {
            System.out.println("  No orders found for this customer.");
            return;
        }

        System.out.println("  Total orders: " + orders.size());
        for (Order o : orders) {
            o.displayOrder();
        }
    }

    public void removeOrder(String customerId, String orderId) {
        List<Order> orders = orderHistory.get(customerId);
        if (orders != null) {
            orders.removeIf(o -> o.getOrderId().equals(orderId));
            System.out.println("[-] Removed order " + orderId
                    + " from customer " + customerId);
        }
    }

    public void showAllOrderSummary() {
        System.out.println("\n=== All Customer Order Summary ===");
        if (orderHistory.isEmpty()) {
            System.out.println("  No orders recorded yet.");
            return;
        }
        for (Map.Entry<String, List<Order>> entry : orderHistory.entrySet()) {
            System.out.println("  Customer ID : " + entry.getKey()
                    + " | Orders: " + entry.getValue().size());
        }
    }

    public void displayAllProducts() {
        System.out.println("\n========================================");
        System.out.println("   " + storeName + " — Full Inventory");
        System.out.println("========================================");
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
        List<Product> filtered = getProductsByCategory(category);
        if (filtered.isEmpty()) {
            System.out.println("  No products found in this category.");
        }
        for (Product p : filtered) {
            p.displayInfo();
        }
    }

    public int getTotalProducts() {
        return inventory.size();
    }

    public String getStoreName() {
        return storeName;
    }
}
