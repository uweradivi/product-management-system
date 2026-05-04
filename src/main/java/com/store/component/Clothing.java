package com.store.component;

public class Clothing extends Product {

    private static final long serialVersionUID = 1L;
    private String size;
    private String material;

    public Clothing(String productId, String name, double price,
                    int stockQuantity, String size, String material) {
        super(productId, name, price, stockQuantity);
        this.size     = size;
        this.material = material;
    }

    @Override public String getCategory()   { return "Clothing & Apparel"; }
    @Override public double applyDiscount() { return getPrice() * 0.15; } // 15% off

    @Override
    protected void printExtraInfo() {
        System.out.println("  | Size     : " + size);
        System.out.println("  | Material : " + material);
    }

    @Override
    public String toCsv() {
        return "CLOTHING," + getProductId() + "," + getName() + ","
                + getPrice() + "," + getStockQuantity() + "," + size + "," + material;
    }

    public String getSize()     { return size; }
    public String getMaterial() { return material; }
}
