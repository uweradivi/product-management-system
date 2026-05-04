package com.store.auth;

/**
 * User roles for role-based access control.
 *
 * ADMIN    — full system control: products, customers, sales, logs, user management
 * STAFF    — operations: add/restock products, process sales, view customers
 * CUSTOMER — online shopper: browse products, buy, view own orders & wallet
 */
public enum Role {
    ADMIN,
    STAFF,
    CUSTOMER
}
