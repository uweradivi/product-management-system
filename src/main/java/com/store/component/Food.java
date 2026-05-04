package com.store.component;

public class Food extends Product {

    private static final long serialVersionUID = 1L;
    private String expiryDate;

    public Food(String productId, String name, double price,
                int stockQuantity, String expiryDate) {
        super(productId, name, price, stockQuantity);
        this.expiryDate = expiryDate;
    }

    @Override public String getCategory()   { return "Food & Grocery"; }
    @Override public double applyDiscount() { return getPrice() * 0.05; } // 5% off

    @Override
    protected void printExtraInfo() {
        System.out.println("  | Expiry   : " + expiryDate);
    }

    @Override
    public String toCsv() {
        return "FOOD," + getProductId() + "," + getName() + ","
                + getPrice() + "," + getStockQuantity() + "," + expiryDate;
    }

    public String getExpiryDate() { return expiryDate; }
}
