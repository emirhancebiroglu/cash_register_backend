package bit.salesservice.exceptions.notinstocks;

/**
 * Exception thrown when a product is not in stock.
 */
public class NotInStocksException extends RuntimeException {
    /**
     * Constructs a new NotInStocksException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NotInStocksException(String message) {
        super(message);
    }
}
