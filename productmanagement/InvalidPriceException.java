
public class InvalidPriceException extends RuntimeException {

    private double attemptedPrice;

    public InvalidPriceException(double attemptedPrice) {
        super("INVALID PRICE: Price cannot be $" + attemptedPrice
                + ". Price must be greater than zero.");
        this.attemptedPrice = attemptedPrice;
    }

    public double getAttemptedPrice() {
        return attemptedPrice;
    }
}
