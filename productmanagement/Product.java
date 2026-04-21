package productmanagement;

/**
 * ============================================================
 *  ABSTRACTION + ENCAPSULATION
 * ============================================================
 *  Product is an abstract class — you cannot create a generic
 *  "Product" object directly. You must create a specific type
 *  (Electronics, Food, Clothing). This mirrors real life:
 *  in a store, everything is a *specific* kind of product.
 *
 *  All fields are PRIVATE (encapsulation). Outside code must
 *  use getters/setters to read or change them.
 * ============================================================
 */
public abstract class Product {

    // --- ENCAPSULATION: private fields ---
    private String productId;
    private String name;
    private double price;
    private int stockQuantity;

    // Constructor
    public Product(String productId, String name, double price, int stockQuantity) {
        this.productId     = productId;
        this.name          = name;
        this.price         = price;
        this.stockQuantity = stockQuantity;
    }

    // --- ABSTRACTION: abstract method ---
    // Every product type MUST implement this — but each does it differently.
    public abstract String getCategory();

    // --- ABSTRACTION: abstract method ---
    // Each product calculates its discount in its own way.
    public abstract double applyDiscount();

    // --- Shared concrete method (inherited by all subclasses) ---
    public void displayInfo() {
        System.out.println("------------------------------");
        System.out.println("  ID       : " + productId);
        System.out.println("  Name     : " + name);
        System.out.println("  Category : " + getCategory());
        System.out.printf ("  Price    : $%.2f%n", price);
        System.out.printf ("  Discount : $%.2f%n", applyDiscount());
        System.out.printf ("  Final    : $%.2f%n", price - applyDiscount());
        System.out.println("  Stock    : " + stockQuantity + " units");
        System.out.println("------------------------------");
    }

    // Restock the product
    public void restock(int quantity) {
        if (quantity > 0) {
            this.stockQuantity += quantity;
            System.out.println("Restocked " + name + " by " + quantity + " units. New stock: " + stockQuantity);
        }
    }

    // Reduce stock when a customer buys
    public boolean sell(int quantity) {
        if (quantity <= stockQuantity) {
            stockQuantity -= quantity;
            System.out.println("Sold " + quantity + " x " + name + ". Remaining: " + stockQuantity);
            return true;
        }
        System.out.println("Not enough stock for " + name + ". Available: " + stockQuantity);
        return false;
    }

    // --- GETTERS & SETTERS (Encapsulation) ---
    public String getProductId()    { return productId; }
    public String getName()         { return name; }
    public double getPrice()        { return price; }
    public int    getStockQuantity(){ return stockQuantity; }

    public void setPrice(double price) {
        if (price >= 0) this.price = price;
    }
    public void setStockQuantity(int qty) {
        if (qty >= 0) this.stockQuantity = qty;
    }
}
