package productmanagement;

public class Electronics extends Product {

    private int warrantyYears;

    public Electronics(String productId, String name, double price,
            int stockQuantity, int warrantyYears) {

        super(productId, name, price, stockQuantity);
        this.warrantyYears = warrantyYears;
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }

    @Override
    public double applyDiscount() {
        return getPrice() * 0.10;
    }

    public void showWarranty() {
        System.out.println(getName() + " comes with a " + warrantyYears + "-year warranty.");
    }

    public int getWarrantyYears() {
        return warrantyYears;
    }
}
