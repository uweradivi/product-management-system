package com.fooddelivery.model;

/**
 * Abstract base class representing any person in the system.
 * Demonstrates: ABSTRACTION (abstract class + abstract method)
 *               ENCAPSULATION (private fields + getters/setters)
 */
public abstract class Person {

    // ENCAPSULATION: private fields hidden from outside
    private String id;
    private String name;
    private String email;
    private String phoneNumber;

    // Constructor
    public Person(String id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // ABSTRACTION: every person must define their role
    public abstract String getRole();

    // ABSTRACTION: every person interacts with the system differently
    public abstract void displayInfo();

    // ENCAPSULATION: controlled access via getters/setters
    public String getId()                        { return id; }
    public String getName()                      { return name; }
    public String getEmail()                     { return email; }
    public String getPhoneNumber()               { return phoneNumber; }

    public void setName(String name)             { this.name = name; }
    public void setEmail(String email)           { this.email = email; }
    public void setPhoneNumber(String phone)     { this.phoneNumber = phone; }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s | Phone: %s",
                getRole(), id, name, phoneNumber);
    }
}
