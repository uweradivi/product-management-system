
public class Food extends Product {

    // Extra field specific to Food
    private String expiryDate; // e.g. "2025-12-31"

    public Food(String productId, String name, double price,
            int stockQuantity, String expiryDate) {
        super(productId, name, price, stockQuantity);
        this.expiryDate = expiryDate;
    }

    // POLYMORPHISM: returns "Food" as category
    @Override
    public String getCategory() {
        return "Food & Grocery";
    }

    // POLYMORPHISM: Food gets only 5% off
    @Override
    public double applyDiscount() {
        return getPrice() * 0.05;
    }

    // Food-specific method
    public void checkExpiry() {
        System.out.println(getName() + " expires on: " + expiryDate);
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
