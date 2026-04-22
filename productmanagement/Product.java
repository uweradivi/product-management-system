
public abstract class Product {

    private String productId;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(String productId, String name, double price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public abstract String getCategory();

    public abstract double applyDiscount();

    public void displayInfo() {
        System.out.println("------------------------------");
        System.out.println("  ID       : " + productId);
        System.out.println("  Name     : " + name);
        System.out.println("  Category : " + getCategory());
        System.out.printf("  Price    : $%.2f%n", price);
        System.out.printf("  Discount : $%.2f%n", applyDiscount());
        System.out.printf("  Final    : $%.2f%n", price - applyDiscount());
        System.out.println("  Stock    : " + stockQuantity + " units");
        System.out.println("------------------------------");
    }

    public void restock(int quantity) {
        if (quantity > 0) {
            this.stockQuantity += quantity;
            System.out.println("Restocked " + name + " by " + quantity + " units. New stock: " + stockQuantity);
        }
    }

    public boolean sell(int quantity) {
        if (quantity <= stockQuantity) {
            stockQuantity -= quantity;
            System.out.println("Sold " + quantity + " x " + name + ". Remaining: " + stockQuantity);
            return true;
        }
        System.out.println("Not enough stock for " + name + ". Available: " + stockQuantity);
        return false;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setPrice(double price) {
        if (price >= 0)
            this.price = price;
    }

    public void setStockQuantity(int qty) {
        if (qty >= 0)
            this.stockQuantity = qty;
    }
}
