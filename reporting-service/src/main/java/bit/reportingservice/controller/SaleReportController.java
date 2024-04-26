package bit.reportingservice.controller;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for handling sale report-related HTTP requests.
 */
@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
public class SaleReportController {
    private final ReportingService reportingService;

    @GetMapping("/list-reports")
    public List<ListReportsReq> listReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String cancelledStatus,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(defaultValue = "completedDate", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortOrder
    ){
        return reportingService.listReports(page, size, cancelledStatus, paymentStatus, sortBy, sortOrder);
    }

    /**
     * Generates a PDF receipt for the specified sale report.
     *
     * @param reportId the ID of the sale report
     * @return a ResponseEntity containing the PDF receipt bytes
     * @throws IOException if an I/O error occurs during PDF generation
     */
    @GetMapping("/generate-receipt/{reportId}")
    public ResponseEntity<byte[]> generatePdfReceipt(@PathVariable Long reportId) throws IOException {
        byte[] pdfBytes = reportingService.generatePdfReceipt(reportId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "sale" + reportId + ".pdf");
        headers.setContentLength(pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}