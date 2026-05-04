package productmanagement;

public class Clothing extends Product {

    private String size;
    private String material;

    public Clothing(String productId, String name, double price,
            int stockQuantity, String size, String material) {
        super(productId, name, price, stockQuantity);
        this.size = size;
        this.material = material;
    }

    @Override
    public String getCategory() {
        return "Clothing & Apparel";
    }

    @Override
    public double applyDiscount() {
        return getPrice() * 0.15;
    }

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
