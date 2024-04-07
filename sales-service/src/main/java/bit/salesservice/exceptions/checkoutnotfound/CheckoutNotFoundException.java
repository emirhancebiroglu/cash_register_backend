package bit.salesservice.exceptions.checkoutnotfound;

public class CheckoutNotFoundException extends RuntimeException {
    public CheckoutNotFoundException(String message) {
        super(message);
    }
}
