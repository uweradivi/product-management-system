package productmanagement;

public class OutOfStockException extends RuntimeException {

    private String productName;
    private int requested;
    private int available;

    public OutOfStockException(String productName, int requested, int available) {
        super("OUT OF STOCK: Cannot sell " + requested + " unit(s) of '" + productName
                + "'. Only " + available + " unit(s) available.");
        this.productName = productName;
        this.requested = requested;
        this.available = available;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}