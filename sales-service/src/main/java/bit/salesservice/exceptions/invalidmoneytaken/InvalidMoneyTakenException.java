package bit.salesservice.exceptions.invalidmoneytaken;

/**
 * Exception thrown when an invalid amount of money taken is encountered.
 */
public class InvalidMoneyTakenException extends RuntimeException {
    /**
     * Constructs a new InvalidMoneyTakenException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidMoneyTakenException(String message) {
        super(message);
    }
}
