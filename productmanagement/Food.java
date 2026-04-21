package productmanagement;

public class Food extends Product {

    private String expiryDate;

    public Food(String productId, String name, double price,
            int stockQuantity, String expiryDate) {
        super(productId, name, price, stockQuantity);
        this.expiryDate = expiryDate;
    }

    @Override
    public String getCategory() {
        return "Food & Grocery";
    }

    @Override
    public double applyDiscount() {
        return getPrice() * 0.05;
    }

    public void checkExpiry() {
        System.out.println(getName() + " expires on: " + expiryDate);
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
