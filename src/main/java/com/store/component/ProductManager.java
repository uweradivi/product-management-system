package com.store.component;

import com.store.util.AppLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================
 *  PRODUCT MANAGER
 *  Central manager for inventory and customers.
 *  Uses the generic Repository<T> for type-safe storage.
 *  Delegates all file persistence to FileManager.
 *  Integrates AppLogger for operation tracking.
 * ============================================================
 */
public class ProductManager {

    private final Repository<Product>  productRepo;
    private final Repository<Customer> customerRepo;
    private final String               storeName;
    private final FileManager          fileManager;

    public ProductManager(String storeName) {
        this.storeName    = storeName;
        this.fileManager  = new FileManager();
        this.productRepo  = new Repository<>();
        this.customerRepo = new Repository<>();
        AppLogger.info("ProductManager initializing for store: " + storeName);
        productRepo.setAll(fileManager.loadProducts());
        customerRepo.setAll(fileManager.loadCustomers());
        AppLogger.info("Loaded " + productRepo.size() + " products and "
                + customerRepo.size() + " customers from disk.");
    }

    // ── Products ──────────────────────────────────────────

    public boolean addProduct(Product product) {
        if (findProductById(product.getProductId()) != null) {
            AppLogger.warn("Duplicate product ID rejected: " + product.getProductId());
            return false;
        }
        productRepo.add(product);
        fileManager.saveProducts(productRepo.getAll());
        AppLogger.success("Product added: [" + product.getProductId() + "] "
                + product.getName() + " — " + product.getCategory()
                + " @ $" + String.format("%.2f", product.getPrice()));
        return true;
    }

    public boolean removeProduct(String productId) {
        Product p = findProductById(productId);
        if (p == null) {
            AppLogger.warn("Remove failed — product not found: " + productId);
            return false;
        }
        productRepo.remove(p);
        fileManager.saveProducts(productRepo.getAll());
        AppLogger.success("Product removed: [" + productId + "] " + p.getName());
        return true;
    }

    public Product findProductById(String id) {
        return productRepo.findFirst(p -> p.getProductId().equalsIgnoreCase(id));
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) return productRepo.getAll();
        String q = query.trim().toLowerCase();
        List<Product> results = productRepo.filter(p ->
                p.getName().toLowerCase().contains(q) ||
                p.getProductId().toLowerCase().contains(q) ||
                p.getCategory().toLowerCase().contains(q));
        AppLogger.info("Product search [\"" + query + "\"] → " + results.size() + " result(s)");
        return results;
    }

    public List<Product> getSortedProducts(String sortBy) {
        List<Product> list = new ArrayList<>(productRepo.getAll());
        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "name":       list.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)); break;
            case "price asc":  list.sort(Comparator.comparingDouble(Product::getPrice)); break;
            case "price desc": list.sort(Comparator.comparingDouble(Product::getPrice).reversed()); break;
            case "stock":      list.sort(Comparator.comparingInt(Product::getStockQuantity)); break;
            case "category":   list.sort(Comparator.comparing(Product::getCategory, String.CASE_INSENSITIVE_ORDER)); break;
            default: break;
        }
        return list;
    }

    public void restockProduct(String productId, int quantity) {
        Product p = findProductById(productId);
        if (p == null) throw new IllegalArgumentException("Product not found: " + productId);
        p.restock(quantity);
        fileManager.saveProducts(productRepo.getAll());
        AppLogger.success("Restocked [" + productId + "] " + p.getName()
                + " by " + quantity + " units → new stock: " + p.getStockQuantity());
    }

    public void updateProductPrice(String productId, double newPrice) {
        Product p = findProductById(productId);
        if (p == null) throw new IllegalArgumentException("Product not found: " + productId);
        double oldPrice = p.getPrice();
        p.setPrice(newPrice);
        fileManager.saveProducts(productRepo.getAll());
        AppLogger.success("Price updated [" + productId + "] " + p.getName()
                + ": $" + String.format("%.2f", oldPrice)
                + " → $" + String.format("%.2f", newPrice));
    }

    public List<Product> getInventory()             { return productRepo.getAll(); }
    public List<Product> getLowStockProducts(int t) { return productRepo.filter(p -> p.getStockQuantity() < t); }

    // ── Customers ─────────────────────────────────────────

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            AppLogger.warn("Duplicate customer ID rejected: " + customer.getCustomerId());
            return false;
        }
        customerRepo.add(customer);
        fileManager.saveCustomers(customerRepo.getAll());
        AppLogger.success("Customer registered: [" + customer.getCustomerId() + "] "
                + customer.getName() + " <" + customer.getEmail() + ">"
                + " wallet=$" + String.format("%.2f", customer.getWalletBalance()));
        return true;
    }

    public boolean removeCustomer(String customerId) {
        Customer c = findCustomerById(customerId);
        if (c == null) {
            AppLogger.warn("Remove failed — customer not found: " + customerId);
            return false;
        }
        customerRepo.remove(c);
        fileManager.saveCustomers(customerRepo.getAll());
        AppLogger.success("Customer removed: [" + customerId + "] " + c.getName());
        return true;
    }

    public Customer findCustomerById(String id) {
        return customerRepo.findFirst(c -> c.getCustomerId().equalsIgnoreCase(id));
    }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) return customerRepo.getAll();
        String q = query.trim().toLowerCase();
        return customerRepo.filter(c ->
                c.getName().toLowerCase().contains(q) ||
                c.getCustomerId().toLowerCase().contains(q) ||
                c.getEmail().toLowerCase().contains(q));
    }

    public void topUpCustomerWallet(String customerId, double amount) {
        Customer c = findCustomerById(customerId);
        if (c == null) throw new IllegalArgumentException("Customer not found: " + customerId);
        c.topUpWallet(amount);
        fileManager.saveCustomers(customerRepo.getAll());
        AppLogger.success("Wallet top-up: [" + customerId + "] " + c.getName()
                + " +$" + String.format("%.2f", amount)
                + " → balance: $" + String.format("%.2f", c.getWalletBalance()));
    }

    public List<Customer> getCustomers() { return customerRepo.getAll(); }

    // ── Sales ──────────────────────────────────────────────

    public void processSale(String customerId, String productId, int qty) {
        Customer customer = findCustomerById(customerId);
        Product  product  = findProductById(productId);
        if (customer == null) throw new IllegalArgumentException("Customer not found: " + customerId);
        if (product  == null) throw new IllegalArgumentException("Product not found: " + productId);
        if (qty <= 0)         throw new IllegalArgumentException("Quantity must be at least 1.");

        double total = (product.getPrice() - product.applyDiscount()) * qty;
        customer.purchase(product, qty);   // may throw OutOfStockException / InsufficientBalanceException
        fileManager.logSale(customer.getName(), product.getName(), qty, total);
        fileManager.saveProducts(productRepo.getAll());
        fileManager.saveCustomers(customerRepo.getAll());
        AppLogger.success("SALE: " + customer.getName() + " bought " + qty
                + " x " + product.getName()
                + " for $" + String.format("%.2f", total));
    }

    // ── Reports ────────────────────────────────────────────

    public String getSalesLog()            { return fileManager.getSalesLogText(); }
    public int    getTotalProducts()       { return productRepo.size(); }
    public int    getTotalCustomers()      { return customerRepo.size(); }
    public String getStoreName()           { return storeName; }
    public List<Product> getAllProducts()  { return productRepo.getAll(); }

    /** Persists both products and customers to disk. */
    public void saveState() {
        fileManager.saveProducts(productRepo.getAll());
        fileManager.saveCustomers(customerRepo.getAll());
    }

    public double getTotalInventoryValue() {
        double total = 0;
        for (Product p : productRepo.getAll()) total += p.getPrice() * p.getStockQuantity();
        return total;
    }

    public long countByCategory(String category) {
        return productRepo.filter(p -> p.getCategory().equalsIgnoreCase(category)).size();
    }

    // ── CSV Export (Bonus Feature) ─────────────────────────

    /**
     * Exports the current inventory to a readable CSV string.
     */
    public String exportInventoryCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type,ID,Name,Category,Price,Discount,FinalPrice,Stock,Extra\n");
        for (Product p : productRepo.getAll()) {
            double finalPrice = p.getPrice() - p.applyDiscount();
            String extra = "";
            if (p instanceof Electronics) extra = ((Electronics) p).getWarrantyYears() + "yr warranty";
            else if (p instanceof Food)   extra = "Exp: " + ((Food) p).getExpiryDate();
            else if (p instanceof Clothing) extra = ((Clothing) p).getSize() + " " + ((Clothing) p).getMaterial();

            sb.append(p.getCategory()).append(",")
              .append(p.getProductId()).append(",")
              .append(p.getName()).append(",")
              .append(p.getCategory()).append(",")
              .append(String.format("%.2f", p.getPrice())).append(",")
              .append(String.format("%.2f", p.applyDiscount())).append(",")
              .append(String.format("%.2f", finalPrice)).append(",")
              .append(p.getStockQuantity()).append(",")
              .append(extra).append("\n");
        }
        AppLogger.info("Inventory CSV export generated: " + productRepo.size() + " products");
        return sb.toString();
    }

    /**
     * Exports all customers to a CSV string.
     */
    public String exportCustomersCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Email,WalletBalance\n");
        for (Customer c : customerRepo.getAll()) {
            sb.append(c.getCustomerId()).append(",")
              .append(c.getName()).append(",")
              .append(c.getEmail()).append(",")
              .append(String.format("%.2f", c.getWalletBalance())).append("\n");
        }
        AppLogger.info("Customers CSV export generated: " + customerRepo.size() + " customers");
        return sb.toString();
    }
}
// These were added to support the CustomerDashboard
