
public class Electronics extends Product {

    // Extra field specific to Electronics
    private int warrantyYears;

    public Electronics(String productId, String name, double price,
            int stockQuantity, int warrantyYears) {
        // Call the parent (Product) constructor
        super(productId, name, price, stockQuantity);
        this.warrantyYears = warrantyYears;
    }

    // POLYMORPHISM: our own version of getCategory()
    @Override
    public String getCategory() {
        return "Electronics";
    }

    // POLYMORPHISM: Electronics get 10% off
    @Override
    public double applyDiscount() {
        return getPrice() * 0.10;
    }

    // Electronics-specific method
    public void showWarranty() {
        System.out.println(getName() + " comes with a " + warrantyYears + "-year warranty.");
    }

    public int getWarrantyYears() {
        return warrantyYears;
    }
}
