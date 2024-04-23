package bit.reportingservice.exceptions.productnotfound;

/**
 * Custom exception to indicate that a product is not found.
 */
public class ProductNotFoundException extends RuntimeException {
    /**
     * Constructs a new ProductNotFoundException with the specified detail message.
     * @param message the detail message.
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
