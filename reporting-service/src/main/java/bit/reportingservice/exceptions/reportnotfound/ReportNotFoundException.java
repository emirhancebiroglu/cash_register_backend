package bit.reportingservice.exceptions.reportnotfound;

/**
 * Custom exception to indicate that a report is not found.
 */
public class ReportNotFoundException extends RuntimeException {
    /**
     * Constructs a new ReportNotFoundException with the specified detail message.
     * @param message the detail message.
     */
    public ReportNotFoundException(String message) {
        super(message);
    }
}
