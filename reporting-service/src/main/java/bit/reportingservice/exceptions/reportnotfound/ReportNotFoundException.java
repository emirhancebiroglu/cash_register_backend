package bit.reportingservice.exceptions.reportnotfound;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}
