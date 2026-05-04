package com.store.component;

public class InsufficientBalanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String customerName;
    private double required;
    private double available;

    public InsufficientBalanceException(String customerName, double required, double available) {
        super("INSUFFICIENT BALANCE: " + customerName + " needs $"
                + String.format("%.2f", required) + " but only has $"
                + String.format("%.2f", available) + " in wallet.");
        this.customerName = customerName;
        this.required     = required;
        this.available    = available;
    }

    public String getCustomerName() { return customerName; }
    public double getRequired()     { return required; }
    public double getAvailable()    { return available; }
}
