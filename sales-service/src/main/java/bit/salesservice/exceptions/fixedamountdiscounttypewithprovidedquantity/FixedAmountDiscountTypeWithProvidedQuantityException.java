package bit.salesservice.exceptions.fixedamountdiscounttypewithprovidedquantity;

/**
 * Exception class for Fixed Amount Discount Type with Provided Quantity.
 * This exception is thrown when an error occurs while applying a fixed amount discount with a provided quantity.
 */
public class FixedAmountDiscountTypeWithProvidedQuantityException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public FixedAmountDiscountTypeWithProvidedQuantityException(String message) {
        super(message);
    }
}
