package bit.salesservice.exceptions.notinstocks;

public class NotInStocksException extends RuntimeException {
    public NotInStocksException(String message) {
        super(message);
    }
}
