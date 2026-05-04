package com.store.component;

public class InvalidPriceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private double attemptedPrice;

    public InvalidPriceException(double attemptedPrice) {
        super("INVALID PRICE: Price cannot be $" + attemptedPrice
                + ". Price must be greater than zero.");
        this.attemptedPrice = attemptedPrice;
    }

    public double getAttemptedPrice() { return attemptedPrice; }
}
