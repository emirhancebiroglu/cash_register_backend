package bit.salesservice.exceptions.invaliddiscountamount;

/**
 * Thrown when an invalid discount amount is provided.
 */
public class InvalidDiscountAmountException extends RuntimeException {

    /**
     * Constructs an {@code InvalidDiscountAmountException} with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidDiscountAmountException(String message) {
        super(message);
    }
}
