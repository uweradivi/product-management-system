
public class Clothing extends Product {

    private String size; // e.g. "M", "L", "XL"
    private String material; // e.g. "Cotton", "Polyester"

    public Clothing(String productId, String name, double price,
            int stockQuantity, String size, String material) {
        super(productId, name, price, stockQuantity);
        this.size = size;
        this.material = material;
    }

    // POLYMORPHISM: returns "Clothing" as category
    @Override
    public String getCategory() {
        return "Clothing & Apparel";
    }

    // POLYMORPHISM: Clothing gets 15% seasonal discount
    @Override
    public double applyDiscount() {
        return getPrice() * 0.15;
    }

    // Clothing-specific method
    public void showDetails() {
        System.out.println(getName() + " — Size: " + size + ", Material: " + material);
    }

    public String getSize() {
        return size;
    }

    public String getMaterial() {
        return material;
    }
}
