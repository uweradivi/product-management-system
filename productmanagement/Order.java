
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {

    private String orderId;
    private String customerName;
    private String productName;
    private int quantity;
    private double totalCost;
    private String orderDate;

    public Order(String orderId, String customerName,
            String productName, int quantity, double totalCost) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.totalCost = totalCost;
        // Automatically record the date and time of the order
        this.orderDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void displayOrder() {
        System.out.println("  Order ID   : " + orderId);
        System.out.println("  Customer   : " + customerName);
        System.out.println("  Product    : " + productName);
        System.out.println("  Quantity   : " + quantity);
        System.out.printf("  Total Cost : $%.2f%n", totalCost);
        System.out.println("  Date       : " + orderDate);
        System.out.println("  ------------");
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getOrderDate() {
        return orderDate;
    }
}
