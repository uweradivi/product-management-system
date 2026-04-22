
public class ProductNotFoundException extends RuntimeException {

    private String searchTerm;

    public ProductNotFoundException(String searchTerm) {
        super("PRODUCT NOT FOUND: No product matching '" + searchTerm + "' exists in the inventory.");
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
