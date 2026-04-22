
public class Customer {

    private String customerId;
    private String name;
    private String email;
    private double walletBalance;

    public Customer(String customerId, String name, String email, double walletBalance) {
        if (name == null || name.trim().isEmpty())
            throw new NullPointerException("Customer name cannot be null or empty.");
        if (walletBalance < 0)
            throw new IllegalArgumentException("Wallet balance cannot be negative. Got: " + walletBalance);

        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.walletBalance = walletBalance;
    }

    /**
     * Purchase a product and record the order in the Map.
     * 
     * @param store — needed to record order into Map
     */
    public void purchase(Product product, int quantity, ProductManager store) {
        double totalCost = (product.getPrice() - product.applyDiscount()) * quantity;

        System.out.println("\n[PURCHASE] " + name + " wants " + quantity
                + " x " + product.getName());
        System.out.printf("  Cost: $%.2f | Wallet: $%.2f%n", totalCost, walletBalance);

        // CUSTOM EXCEPTION: InsufficientBalanceException
        if (walletBalance < totalCost)
            throw new InsufficientBalanceException(name, totalCost, walletBalance);

        // CUSTOM EXCEPTION: OutOfStockException (via product.sell)
        product.sell(quantity);
        walletBalance -= totalCost;

        // Record this order in the Map (customer ID → list of orders)
        store.recordOrder(customerId, name, product.getName(), quantity, totalCost);

        System.out.printf("  SUCCESSFUL! Remaining balance: $%.2f%n", walletBalance);
    }

    public void showProfile() {
        System.out.println("=== Customer: " + name + " ===");
        System.out.println("  ID     : " + customerId);
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
