package com.store.component;

public class Electronics extends Product {

    private static final long serialVersionUID = 1L;
    private int warrantyYears;

    public Electronics(String productId, String name, double price,
                       int stockQuantity, int warrantyYears) {
        super(productId, name, price, stockQuantity);
        this.warrantyYears = warrantyYears;
    }

    @Override public String getCategory()    { return "Electronics"; }
    @Override public double applyDiscount()  { return getPrice() * 0.10; } // 10% off

    @Override
    protected void printExtraInfo() {
        System.out.println("  | Warranty : " + warrantyYears + " year(s)");
    }

    @Override
    public String toCsv() {
        return "ELECTRONICS," + getProductId() + "," + getName() + ","
                + getPrice() + "," + getStockQuantity() + "," + warrantyYears;
    }

    public int getWarrantyYears() { return warrantyYears; }
}
