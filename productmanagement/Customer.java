package productmanagement;

public class Customer {

    private String customerId;
    private String name;
    private String email;
    private double walletBalance;

    public Customer(String customerId, String name, String email, double walletBalance) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.walletBalance = walletBalance;
    }

    public void purchase(Product product, int quantity) {
        double totalCost = (product.getPrice() - product.applyDiscount()) * quantity;

        System.out.println("\n[PURCHASE] " + name + " wants to buy " + quantity + " x " + product.getName());
        System.out.printf("  Total cost (after discount): $%.2f%n", totalCost);
        System.out.printf("  Wallet balance: $%.2f%n", walletBalance);

        if (walletBalance >= totalCost) {
            boolean success = product.sell(quantity);
            if (success) {
                walletBalance -= totalCost;
                System.out.printf("  Purchase SUCCESSFUL! Remaining balance: $%.2f%n", walletBalance);
            }
        } else {
            System.out.println("  Purchase FAILED: Insufficient wallet balance.");
        }
    }

    public void showProfile() {
        System.out.println("=== Customer Profile ===");
        System.out.println("  ID     : " + customerId);
        System.out.println("  Name   : " + name);
        System.out.println("  Email  : " + email);
        System.out.printf("  Wallet : $%.2f%n", walletBalance);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public double getWalletBalance() {
        return walletBalance;
    }
}
