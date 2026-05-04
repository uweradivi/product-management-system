package com.store.auth;

import java.io.Serializable;

/**
 * Represents an application user with a role.
 * Passwords are stored as SHA-256 hashes — never plain text.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private String       passwordHash;   // SHA-256 hex
    private Role         role;
    private final String createdAt;
    private String       linkedCustomerId; // for CUSTOMER role: their Customer record ID

    public User(String username, String passwordHash, Role role, String createdAt) {
        this.username          = username;
        this.passwordHash      = passwordHash;
        this.role              = role;
        this.createdAt         = createdAt;
        this.linkedCustomerId  = null;
    }

    public User(String username, String passwordHash, Role role, String createdAt, String linkedCustomerId) {
        this.username          = username;
        this.passwordHash      = passwordHash;
        this.role              = role;
        this.createdAt         = createdAt;
        this.linkedCustomerId  = linkedCustomerId;
    }

    /** CSV: username,passwordHash,role,createdAt[,linkedCustomerId] */
    public String toCsv() {
        String base = username + "," + passwordHash + "," + role.name() + "," + createdAt;
        return (linkedCustomerId != null) ? base + "," + linkedCustomerId : base;
    }

    public String getUsername()          { return username; }
    public String getPasswordHash()      { return passwordHash; }
    public Role   getRole()              { return role; }
    public String getCreatedAt()         { return createdAt; }
    public String getLinkedCustomerId()  { return linkedCustomerId; }

    public void setPasswordHash(String hash)        { this.passwordHash = hash; }
    public void setRole(Role role)                  { this.role = role; }
    public void setLinkedCustomerId(String cid)     { this.linkedCustomerId = cid; }

    public boolean isAdmin()    { return role == Role.ADMIN; }
    public boolean isStaff()    { return role == Role.STAFF; }
    public boolean isCustomer() { return role == Role.CUSTOMER; }
}
